module SerieBot
  module Userjoin
    extend Discordrb::Commands::CommandContainer
    extend Discordrb::EventContainer

    def self.get_server_log?(event)
      # Get channel from ID
      return event.bot.channel(Helper.get_xxx_channel?(event, 'srv', 'server-log'))
    end

    def self.get_mod_log?(event)
      # Get channel from ID
      return event.bot.channel(Helper.get_xxx_channel?(event, 'mod', 'mod-log'))
    end

    member_join do |event|
      time = Time.now.getutc
      message_to_send = "User: #{event.user.mention} | **#{event.user.distinct}**\n"
      message_to_send << "Account creation: `#{event.user.creation_time.getutc.asctime} UTC`\n"

      channel = get_server_log?(event)
      unless channel.nil?
        channel.send_embed do |e|
          e.title = 'A user just joined the server!'
          e.description = message_to_send.to_s
          e.colour = '#00C853'
          e.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")
        end
      end
    end

    member_leave do |event|
      time = Time.now.getutc

      # Check if the account was banned or just left.
      user = event.user
      was_banned = false
      event.server.bans.each do |banned_user|
        if banned_user.id == user.id
          # We don't need to announce they're leaving.
          was_banned = true
        end
      end
      unless was_banned
        channel = get_server_log?(event)
        unless channel.nil?
          channel.send_embed do |e|
            e.title = 'A user left the server!'
            e.description = "User: #{event.user.mention} | **#{event.user.distinct}**"
            e.colour = '#FFEB3B'
            e.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")
          end
        end
      end
    end

    user_ban do |event|
      time = Time.now.getutc
      e = Discordrb::Webhooks::Embed.new
      e.title = 'A user was banned from the server!'
      e.description = "User: #{event.user.mention} | **#{event.user.distinct}**"
      e.colour = '#D32F2F'
      e.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")

      channel = get_server_log?(event)
      unless channel.nil?
        channel.send_embed('', e)
      end

      channel = get_mod_log?(event)
      unless channel.nil?
        channel.send_embed('', e)
      end
    end

    user_unban do |event|
      time = Time.now.getutc
      embed_sent = Discordrb::Webhooks::Embed.new
      embed_sent.title = 'A user was unbanned from the server!'
      embed_sent.description = "User: #{event.user.mention} | **#{event.user.distinct}**"
      embed_sent.colour = '#4CAF50'
      embed_sent.footer = Discordrb::Webhooks::EmbedFooter.new(text: "Current UTC time: #{time.strftime('%H:%M')}")

      channel = get_server_log?(event)
      unless channel.nil?
        channel.send_embed('', embed_sent)
      end

      channel = get_mod_log?(event)
      unless channel.nil?
        channel.send_embed('', embed_sent)
      end
    end
  end
end
