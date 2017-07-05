module SerieBot
  module Morpher
    extend Discordrb::Commands::CommandContainer
    extend Discordrb::EventContainer

    class << self
      attr_accessor :original_channel
      attr_accessor :mirrored_channel
      attr_accessor :messages
    end



    def self.setup_channels(event)
      if original_channel.nil? | mirrored_channel.nil?
        @original_channel = Helper.channel_from_name(event.bot.server(Config.root_server), 'announcements')
        # ID of mirror server
        @mirrored_channel = if Config.debug
                              Helper.channel_from_name(event.bot.server(Config.morpher_server), 'dev-test')
                            else
                              Helper.channel_from_name(event.bot.server(Config.morpher_server), 'announcements')
                            end
      end
    end

    def self.create_embed(bot, message)
      embed_sent = Discordrb::Webhooks::Embed.new
      embed_sent.title = 'New announcement!'
      embed_sent.description = Helper.parse_mentions(bot, message.content)
      embed_sent.colour = '#FFEB3B'
      embed_sent.author = Discordrb::Webhooks::EmbedAuthor.new(name: message.author.name,
                                                               url: 'https://rc24.xyz',
                                                               icon_url: Helper.avatar_url(message.author, 32))
      # Format: Sat Feb 11 01:30:45 2017 UTC
      embed_sent.footer = Discordrb::Webhooks::EmbedFooter.new(text: "#{message.timestamp.utc.strftime('%c')} UTC")
      return embed_sent
    end

    def self.create_error_embed(message)
      embed_error = Discordrb::Webhooks::Embed.new
      embed_error.title = 'An error occurred.'
      embed_error.description = message
      embed_error.colour = '#D32F2F'
    end

    message do |event|
      setup_channels(event)
      if event.channel == original_channel
        embed_to_send = create_embed(event.bot, event.message)
        message_to_send = mirrored_channel.send_embed('', embed_to_send)

        # Store message under original id
        @messages[event.message.id] = {
            embed_sent: embed_to_send,
            message_sent: message_to_send.id
        }
        Helper.save_xyz('morpher', Morpher.messages)
      end
    end

    message_edit do |event|
      setup_channels(event)
      if event.channel == original_channel
        # Time to edit the message!
        message_data = @messages[event.message.id]
        if message_data.nil?
          embed_error = create_error_embed("An announcement was edited, but I lost track and couldn't edit my copy.")
          mirrored_channel.send_embed('', embed_error)
        else
          embed = message_data[:embed_sent]
          embed.description = Helper.parse_mentions(event.bot, event.message)
          # Also edit the footer
          # Format: Sat Feb 11 01:30:45 2017 UTC
          embed.footer = Discordrb::Webhooks::EmbedFooter.new(text: "#{event.message.edited_timestamp.utc.strftime('%c')} UTC")
          mirror_message_id = message_data[:message_sent]
          mirrored_channel.message(mirror_message_id).edit('', embed)
        end
        Helper.save_xyz('morpher', Morpher.messages)
      end
    end

    message_delete do |event|
      setup_channels(event)
      if event.channel == original_channel
        # Time to remove the corresponding announcement.
        if @messages[event.id].nil?
          # We can assume this announcement wasn't synced, so no use trying to recover it.
          # Oh well
        else
          mirrored_channel.message(@messages[event.id][:message_sent]).delete
          @messages.delete(event.id)
        end
        Helper.save_xyz('morpher', Morpher.messages)
      end
    end

    # Per PokeAcer's recommendation
    member_join do |event|
      # RC24 News Server
      if event.server.id == Config.morpher_server
        message_to_pm = "___Information Notice___\n"
        message_to_pm += 'Hi! You have joined the RiiConnect24 News Server, for users who are not allowed access to the regular server.'
        message_to_pm += " As such, we do not know whom is in the server, and you may wish to turn off access to Direct Messages between members to ensure users cannot contact you through the public mutual server.\n"
        message_to_pm += "Here's how to do it: http://i.imgur.com/EssBp8d.gifv\n\n"
        message_to_pm += "Regards,\nRiiConnect24"
        event.user.pm(message_to_pm)
      end
    end

    # The following method syncs the announcement channel with the mirror.
    # It's not a command. Call it with eval: #{Config.prefix}eval Morpher.sync_announcements(event)
    # Also, I hope you've already setup the channels and cleared the whole channel.
    def self.sync_announcements(event)
      # Start on first message
      offset_id = original_channel.history(1, 1, 1)[0].id # get first message id

      # Now let's sync!
      loop do
        # We can only go through 100 messages at a time, so grab 100.
        # We need to reverse it because it goes reverse in what we're doing.
        current_history = original_channel.history(100, nil, offset_id).reverse
        # Break if there are no other messages
        break if current_history == []

        # Mirror announcement + save it
        current_history.each do |message|
          next if message.nil?
          embed_to_send = create_embed(event.bot, message)
          message_to_send = mirrored_channel.send_embed('', embed_to_send)

          # Store message under original id
          @messages[message.id] = {
              embed_sent: embed_to_send,
              message_sent: message_to_send.id
          }
        end

        # Set offset ID to last message in history that we saw
        # (this is the last message sent - 1 since Ruby has array offsets of 0)
        offset_id = current_history[current_history.length - 1].id
      end
      Helper.save_xyz('morpher', Morpher.messages)
      return 'Done!'
    end
  end
end