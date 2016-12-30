module SerieBot
	module Logging
		require 'rumoji'
		extend Discordrb::Commands::CommandContainer
		extend Discordrb::EventContainer
    class << self
      attr_accessor :messages
    end
    @messages = Hash.new

		def self.get_message(event, state)
			# Only log messages written for the bot
			#if event.message.content.start_with?(Config.prefix)
	      if (event.channel.private?)
        	server_name = "DM"
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
      	puts "#{state}(#{id}) #{event.message.timestamp.strftime('[%D %H:%M]')} #{server_name}/#{channel_name} <#{event.author.distinct}>: #{content}"
      	puts "<Attachments: #{attachments[0].filename}: #{attachments[0].url} >" unless attachments.empty?
			#end
			# Store message, even if it's not for the bot
			@messages = {
				event.message.id => {
					:message => event.message,
					:channel => channel_name,
					:server => server_name,
				},
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
      message.mentions.each { |x| content = content.gsub("<@#{x.id.to_s}>", "<@#{x.distinct}>") ; content = content.gsub("<@!#{x.id.to_s}>", "\@#{x.distinct}") }

      attachments = message.attachments
      id = Base64.strict_encode64([message.id].pack('L<'))
      puts "/!\\#{state}(#{id}) #{message.timestamp.strftime('[%D %H:%M]')} #{server_name}/#{channel_name} <#{message.author.distinct}> #{content}"
      puts "<Attachments: #{attachments[0].filename}: #{attachments[0].url} >" unless attachments.empty?
    end

		message do |event|
			next if Config.ignored_servers.include?(event.server.id) or !Config.logging rescue nil
			self.get_message(event, nil)
		end

		message_edit do |event|
			next if Config.ignored_servers.include?(event.server.id) or !Config.logging rescue nil
			self.get_message(event, "{EDIT}")
		end

    message_delete do |event|
      next if Config.ignored_servers.include?(event.server.id) or !Config.logging rescue nil
      self.get_deleted_message(event, "{DELETE}")
    end

    member_join do |event|
			next if Config.ignored_servers.include?(event.server.id) or !Config.logging rescue nil
      puts "#{Time.now.strftime('[%D %H:%M]')} #{event.member.distinct} joined #{event.server.name}"
    end

	end
end
