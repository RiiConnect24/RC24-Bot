module SerieBot
    module Logging
        require 'rumoji'
				require 'colorize'
        extend Discordrb::Commands::CommandContainer
        extend Discordrb::EventContainer
        class << self
          attr_accessor :messages
        end
        @messages = {}

        def self.get_message(event, state)
            if event.channel.private?
                server_name = 'DM'
                channel_name = event.channel.name
            else
                server_name = event.server.name
                channel_name = "##{event.channel.name}"
            end
            content = Helper.parse_mentions(event.bot, event.message)
            content = Rumoji.encode(content)
            attachments = event.message.attachments
            id = Base64.strict_encode64([event.message.id].pack('L<'))

            # Format expected:
            # (ID) [D H:M] server name/channel name <author>: message
            puts "#{state}(#{id}) #{event.message.timestamp.strftime('[%D %H:%M]')} #{server_name}/#{channel_name} <#{event.author.distinct}>: #{content}".colorize(:green)
            puts "<Attachments: #{attachments[0].filename}: #{attachments[0].url} >".colorize(:green) unless attachments.empty?

            # Store message
            @messages = {
                event.message.id => {
                    message: event.message,
                    channel: channel_name,
                    server: server_name
                }
            }
        end

        def self.get_deleted_message(event, state)
            if @messages[event.id].nil?
                puts "/!\\{DELETE} Message with ID #{event.id} was deleted, but the contents couldn't be fetched."
                return nil
            end

            message = @messages[event.id][:message]
            channel_name = @messages[event.id][:channel]
            server_name = @messages[event.id][:server]

            content = Rumoji.encode(message.content)
            message.mentions.each { |x| content = content.gsub("<@#{x.id}>", "<@#{x.distinct}>"); content = content.gsub("<@!#{x.id}>", "\@#{x.distinct}") }

            attachments = message.attachments
            id = Base64.strict_encode64([message.id].pack('L<'))
            puts "/!\\#{state}(#{id}) #{message.timestamp.strftime('[%D %H:%M]')} #{server_name}/#{channel_name} <#{message.author.distinct}> #{content}".colorize(:red)
            puts "<Attachments: #{attachments[0].filename}: #{attachments[0].url} >}".colorize(:red) unless attachments.empty?
        end

        message do |event|
            get_message(event, nil) if Config.logging
        end

        message_edit do |event|
            get_message(event, '{EDIT}') if Config.logging
        end

        message_delete do |event|
            get_deleted_message(event, '{DELETE}') if Config.logging
        end

        member_join do |event|
            if Config.logging
                puts "#{Time.now.strftime('[%D %H:%M]')} #{event.member.distinct} joined #{event.server.name}".colorize(:yellow)
          end
        end
    end
end
