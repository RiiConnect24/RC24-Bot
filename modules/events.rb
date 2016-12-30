module SerieBot
	module Events
    extend Discordrb::Commands::CommandContainer
    extend Discordrb::EventContainer

    if Config.use_cleverbot
      require 'cleverbot'
      extend Discordrb::Commands::CommandContainer
      extend Discordrb::EventContainer
      friend = Cleverbot.new(Config.cleverbot_api_user, Config.cleverbot_api_token)

      message do |event|
        if event.message.content.start_with?(event.bot.user(event.bot.profile.id).mention)
          message = event.message.content.slice!(event.bot.user(Config.appid).mention.length, event.message.content.size)
          response = friend.say(message)
          event.respond("💬 #{response}") if !response.nil?
        end
      end

			ready do |event|
  			event.bot.game=Config::playing
  			puts "Bot succesfully launched!"
				event.bot.online
			end

    end
	end
end
