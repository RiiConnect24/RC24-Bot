module SerieBot
	module Mod
		extend Discordrb::Commands::CommandContainer
    class << self
      attr_accessor :actions
    end
    actions = []

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
			clear_num = count.to_i + 1

			begin
				while clear_num > 0
					if clear_num >= 99
						# Welcome back to Workaround city.
						ids = []
						event.channel.history(99).each { |x| ids.push(x.id) }
						Discordrb::API::Channel.bulk_delete_messages(event.bot.token, event.channel.id, ids)
						clear_num -= 99
					else
						ids = []
						event.channel.history(clear_num).each { |x| ids.push(x.id) }
						Discordrb::API::Channel.bulk_delete_messages(event.bot.token, event.channel.id, ids)
						clear_num = 0
					end
				end
				message = event.respond('🚮  Cleared #{original_num} messages!')
				sleep(3)
				message.delete
			rescue Discordrb::Errors::NoPermission
				event.respond("❌ I don't have permission to delete messages!")
				break
			end
			nil
		end

		command(:kick, description: 'Temporarily kick somebody from the server. Mod only.', usage: "#{Config.prefix}kick @user reason", min_args: 2) do |event, *kick_reason|
			unless Helper.is_helper?(event) || Helper.is_moderator?(event) || Helper.is_developer?(event) || Helper.is_admin?(event.user)
				event.respond("❌ You don't have permission for that!")
				break
			end

			member = event.server.member(event.message.mentions[0])
			# Helpers should only be able to kick unverified users.
			if Helper.is_helper?(event)
				# Check if the user has verified.
				if Helper.is_verified?(event, member)
					event.respond("❌ You can't kick verified members!")
					break
				end
			end

			break if event.channel.private?
			if event.message.mentions[0]
				final_message = kick_reason.drop(1)
				display = final_message.join(' ')
				message = "You have been kicked from the server **#{event.server.name}** by #{event.message.author.mention} | **#{event.message.author.display_name}**\n"
				message << "They gave the following reason: ``#{display}``"
				begin
						member.pm(message)
				rescue Discordrb::Errors::NoPermission
						event.respond('Could not DM user about kick reason!')
						break
				end
				begin
        	event.server.kick(member)
				rescue Discordrb::Errors::NoPermission
					event.respond("❗❗❗ The bot doesn't have permission to kick!")
					break
				end
				event.respond('✅ Kicked!')
        # Register for logging
        actions[member.id] = event.user.id
			else
				event.respond('❌ Invalid argument. Please mention a valid user.')
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
				bandisplay = finalbanmessage.join(' ')
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
					event.respond("The bot doesn't have permission to ban that user!")
					break
				end

				# Register for logging
        actions[member.id] = event.user.id


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
