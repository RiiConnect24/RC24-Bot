module SerieBot
	module Utility
		extend Discordrb::Commands::CommandContainer

		command(:avatar, description: "Displays the avatar of a user.") do |event, *mention|
      event.channel.start_typing # Let people know the bot is working on something.
				if mention.nil?
					user = event.message.author
				elsif event.message.mentions[0]
					user = event.server.member(event.message.mentions[0])
				else
					event << "❌ Mention a valid user!"
					next
				end
				avatar_path = Helper.download_avatar(user, "tmp")
				event.channel.send_file File.new([avatar_path].sample)
		end

		command(:info, description: "Displays info about a user.") do |event, *mention|
      event.channel.start_typing
			if event.channel.private? # ignore PMs
				event << "❌ This command can only be used in a server."
				next
			end

			if event.message.mentions[0]
				user = event.message.mentions[0]
				if user.game.nil?
					playing = "[N/A]"
				else
					playing = user.game
				end
				member = user.on(event.server)
        if member.nickname.nil?
					nick = "[N/A]" #
				else
					nick = member.nickname
				end
				event << "👥  Infomation about **#{member.display_name}**"
				event << "-ID: **#{user.id}**"
				event << "-Username: `#{user.distinct}`"
				event << "-Nickname: **#{nick}**"
				event << "-Status: **#{user.status}**"
				event << "-Playing: **#{playing}**"
				event << "-Account created: **#{user.creation_time.getutc.asctime}** UTC"
				event << "-Joined server at: **#{member.joined_at.getutc.asctime}** UTC"
			end
		end

	end
end
