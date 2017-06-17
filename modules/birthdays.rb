module SerieBot
  module Birthdays
    require 'yaml'
    require 'time'
    # doesn't everything take time?
    require 'date'

    extend Discordrb::Commands::CommandContainer
    class << self
      attr_accessor :dates
    end
    RIICONNECT24_SERVER_ID = 206934458954153984
    BIRTHDAY_CHANNEL = 315973215904071681

    def self.sleeping_beauty(bot)
      sleep(Time.parse('23:59:59') - Time.now)
      check_for_birthdays(Date.today, bot)
      # yeah, a tad recursive
      sleeping_beauty(bot)
    end


    def self.check_for_birthdays(date = Date.today, bot)
      format = "#{date.mon}-#{date.mday}"
      unless @dates[format].nil?
        @dates[format].each do |id|
          person = bot.user(id)
          embed_sent = Discordrb::Webhooks::Embed.new
          embed_sent.title = 'Happy birthday! 🎂'
          embed_sent.description = "Please send them messages wishing them a happy birthday here on Discord and/or birthday mail on their Wii if you've registered them!"
          # It's a blue enough color.
          embed_sent.colour = '#00a6e9'
          embed_sent.author = Discordrb::Webhooks::EmbedAuthor.new(name: "It's #{person.name}'s birthday!",
                                                                   url: 'https://rc24.xyz',
                                                                   icon_url: Helper.avatar_url(person, 32))

          bot.channel(BIRTHDAY_CHANNEL, bot.server(RIICONNECT24_SERVER_ID)).send_embed('', embed_sent)
        end
      end
    end

    command(:birthday) do |event, *args|
      date_string = args.join(' ')
      begin
        date = Date.parse(date_string)

        format = "#{date.mon}-#{date.mday}"
        if @dates[format].nil?
          @dates[format] = []
        end
        @dates[format] << event.user.id
        Helper.save_all
        event.respond('✅ Updated successfully!')
      rescue
        event.respond("I couldn't parse your date. Try something like April 20th, 2017, instead of 4/20/17.")
      end
    end
  end
end