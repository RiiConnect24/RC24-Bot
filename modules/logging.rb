module SerieBot
    module Logging
        require 'rumoji'
        require 'rainbow'
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
            log_message = "#{state}(#{id}) #{event.message.timestamp.utc.strftime('[%D %H:%M]')} #{server_name}/#{channel_name} <#{event.author.distinct}>: #{content}"
            attachment_message = "<Attachments: #{attachments[0].filename}: #{attachments[0].url} >" unless attachments.empty?
            if state == '{EDIT}'
                puts Rainbow(log_message.to_s).yellow
                puts Rainbow(attachment_message.to_s).yellow unless attachments.empty?
            else
                puts Rainbow(log_message.to_s).green
                puts Rainbow(attachment_message.to_s).green unless attachments.empty?
            end
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
            puts Rainbow("/!\\#{state}(#{id}) #{message.timestamp.utc.strftime('[%D %H:%M]')} #{server_name}/#{channel_name} <#{message.author.distinct}> #{content}").red
            puts Rainbow("<Attachments: #{attachments[0].filename}: #{attachments[0].url} >}").red unless attachments.empty?
        end

        message do |event|
            get_message(event, nil) unless Config.ignore_ids.include? event.channel.id
        end

        message_edit do |event|
            get_message(event, '{EDIT}') unless Config.ignore_ids.include? event.channel.id
        end

        message_delete do |event|
            get_deleted_message(event, '{DELETE}') unless Config.ignore_ids.include? event.channel.id
        end

        member_join do |event|
            puts Rainbow("#{Time.now.utc.strftime('[%D %H:%M]')} #{event.member.distinct} joined #{event.server.name}").blue
        end
    end
end
