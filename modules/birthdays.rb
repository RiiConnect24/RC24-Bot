module SerieBot
  module Birthdays
    require 'yaml'
    require 'time'
    require 'date'

    extend Discordrb::Commands::CommandContainer
    class << self
      attr_accessor :dates
    end
    BIRTHDAY_CHANNEL = 315973215904071681

    def self.sleeping_beauty(bot)
      sleep((Time.parse('23:59:59') - Time.now))
      # Yes, you're reading this right, we're sleeping for a bit more.
      # This is some type of race condition -- from my understanding it fires a few seconds behind.
      # Can't hurt, really.
      sleep(5)

      check_for_birthdays(Date.today, bot)
      # yeah, a tad recursive
      sleeping_beauty(bot)
    end


    def self.check_for_birthdays(date = Date.today, bot)
      format = "#{date.mon}-#{date.mday}"
      unless @dates[format].nil?
        @dates[format].each do |id|
          # The user might've left the server. Check for so.
          if bot.server(Config.root_server).member(id).nil?
            next
          end
          person = bot.user(id)
          embed_sent = Discordrb::Webhooks::Embed.new
          embed_sent.title = 'Happy birthday! 🎂'
          embed_sent.description = "Please send them messages wishing them a happy birthday here on Discord and/or birthday mail on their Wii if you've registered them!"
          # It's a blue enough color.
          embed_sent.colour = '#00a6e9'
          embed_sent.author = Discordrb::Webhooks::EmbedAuthor.new(name: "It's #{person.name}'s birthday!",
                                                                   url: 'https://rc24.xyz',
                                                                   icon_url: Helper.avatar_url(person, 32))

          Helper.channel_from_name(bot.server(Config.root_server), 'birthdays').send_embed('', embed_sent)
        end
      end
    end

    command(:birthday) do |event, *args|
      date_string = args.join(' ')
      begin
        user_id = event.user.id
        date = Date.parse(date_string)

        unless @dates.nil?
          @dates.each do |date_pair|
            next unless @dates[date_pair].is_a?(Array)
            # Remove duplicates
            @dates[date_pair].delete(user_id)
          end
        end

        format = "#{date.mon}-#{date.mday}"
        if @dates[format].nil?
          @dates[format] = []
        end
        @dates[format] << event.user.id
        Helper.save_all
        event.respond('✅ Updated successfully!')
      rescue ArgumentError
        event.respond("I couldn't parse your date. Try something like April 20th, 2017, instead of 4/20/17.")
      rescue
        event.respond("Something went super wrong, please contact a bot owner! See a list at `#{Config.prefix}owners`.")
      end
    end
  end
end