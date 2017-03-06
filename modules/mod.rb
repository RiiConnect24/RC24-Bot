module SerieBot
	module Mod
		extend Discordrb::Commands::CommandContainer
		command(:clear, max_args: 1, required_permissions: [:manage_messages], description: 'Deletes x messages, mod only.', usage: '&clear x') do |event, count|
			Helper.ignore_bots(event)
			if count.nil?
				event.respond('❌ No argument specicied. Enter a valid number!')
				break
			end

			unless /\A\d+\z/ =~ count
				event.respond("`#{count}` is not a valid number!")
				break
			end
			original_num = count.to_i
			clearnum = count.to_i + 1

			begin
				while clearnum > 0
					if clearnum >= 99
						# Welcome back to Workaround city.
						ids = []
						event.channel.history(99).each { |x| ids.push(x.id) }
						Discordrb::API::Channel.bulk_delete_messages(event.bot.token, event.channel.id, ids)
						clearnum -= 99
					else
						ids = []
						event.channel.history(clearnum).each { |x| ids.push(x.id) }
						Discordrb::API::Channel.bulk_delete_messages(event.bot.token, event.channel.id, ids)
						clearnum = 0
					end
				end
				message = event.respond("🚮  Cleared #{original_num} messages!")
				sleep(3)
				message.delete
			rescue Discordrb::Errors::NoPermission
				event.respond("❌ I don't have permission to delete messages!")
				break
			end
			nil
		end

		command(:kick, description: "Temporarily kick somebody from the server. Mod only.", usage: "#{Config.prefix}kick @user reason", min_args: 2) do |event, *kickreason|
			unless Helper.is_moderator?(event) || Helper.is_developer?(event) || Helper.is_admin?(event.user)
				event.respond("❌ You don't have permission for that!")
				break
			end
			member = event.server.member(event.message.mentions[0])

			break if event.channel.private?
			if event.message.mentions[0]
				finalmessage = kickreason.drop(1)
				display = finalmessage.join(" ")
				message = "You have been kicked from the server **#{event.server.name}** by #{event.message.author.mention} | **#{event.message.author.display_name}**\n"
				message << "They gave the following reason: ``#{display}``"
				begin
						member.pm(message)
				rescue Discordrb::Errors::NoPermission
						event.respond("Could not DM user about ban reason!")
						break
				end
				begin
        	event.server.kick(member)
				rescue Discordrb::Errors::NoPermission
					"The bot doesn't have permision to kick!"
					break
				end
			else
				"Invalid argument. Please mention a valid user."
			end
		end

		command(:ban, description: "Permanently ban someone from the server. Mod only.", usage: "#{Config.prefix}ban @user reason", min_args: 2) do |event, *banreason|
			unless Helper.is_moderator?(event) || Helper.is_developer?(event) || Helper.is_admin?(event.user)
				event.respond("❌ You don't have permission for that!")
				break
		  end

			member = event.server.member(event.message.mentions[0])
			break if event.channel.private?
			if event.message.mentions[0]
				finalbanmessage = banreason.drop(1)
				bandisplay = finalbanmessage.join(" ")
				message = "You have been **permanently banned** from the server #{event.server.name} by #{event.message.author.mention} | **#{event.message.author.display_name}**\n"
				message << "They gave the following reason: ``#{bandisplay}``\n\n"
				message << "If you wish to appeal for your ban's removal, please contact this person, or the server owner."
				begin
						member.pm(message)
						event.respond("👌 The ban hammer has hit, hard.")
				rescue Discordrb::Errors::NoPermission
						event.respond("Could not DM user about ban reason!")
						break
				end
				begin
        	event.server.ban(member)
				rescue
					event.respond("The bot doesn't have permision to ban that user!")
					break
				end

				break
			else
				"Invalid argument. Please mention a valid user."
			end
		end

		command(:lockdown) do |event, time, *reason|
			unless Helper.is_developer?(event) || Helper.is_bot_helper?(event) || Helper.is_admin?(event.user)
				event.respond("❌ You don't have permission for that!")
				break
		  end
			reason = reason.join(' ')
			lockdown = Discordrb::Permissions.new
			lockdown.can_send_messages = true
			everyone_role = Helper.role_from_name(event.server, "@everyone")
			event.channel.define_overwrite(everyone_role, 0, lockdown)
			if time.nil?
				event.respond("🔒 **This channel is now in lockdown. Only staff can send messages. **🔒")
			elsif /\A\d+\z/.match(time)
				event.respond("🔒 **This channel is now in lockdown. Only staff can send messages. **🔒\n**Time:** #{time} minute(s)")
				time_sec = time * 60
				sleep(time_sec)
				lockdown = Discordrb::Permissions.new
				lockdown.can_send_messages = true
				everyone_role = Helper.role_from_name(event.server, "@everyone")
				event.channel.define_overwrite(everyone_role, lockdown, 0)
				event.respond(":unlock: **Channel has been unlocked.**:unlock:")
			end
		end

		command(:unlockdown) do |event|
			unless Helper.is_developer?(event) || Helper.is_bot_helper?(event) || Helper.is_admin?(event.user)
				event.respond("❌ You don't have permission for that!")
				break
			end
			lockdown = Discordrb::Permissions.new
			lockdown.can_send_messages = true
			everyone_role = Helper.role_from_name(event.server, "@everyone")
			event.channel.define_overwrite(everyone_role, lockdown, 0)
			event.respond(":unlock: **Channel has been unlocked.**:unlock:")
		end
	end
end
