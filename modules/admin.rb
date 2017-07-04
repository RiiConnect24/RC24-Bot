module SerieBot
  module Admin
    extend Discordrb::Commands::CommandContainer

    command(:setavatar) do |event, *url|
      unless Helper.has_role?(event, [:owner, :dev, :bot])
        event.respond("❌ You don't have permission for that!")
        break
      end
      url = url.join(' ')
      file = Helper.download_file(url, 'tmp')
      event.bot.profile.avatar = File.open(file)
      event.respond('✅ Avatar should be updated!')
    end

    command(:ignore, description: 'Temporarily ignore a given user', min_args: 1, max_args: 1) do |event, mention|
      event.channel.start_typing
      unless Helper.has_role?(event, [:owner, :dev, :mod])
        event.respond("❌ You don't have permission for that!")
        break
      end
      user = begin
        event.bot.parse_mention(mention)
      rescue
        event.respond("❌ `#{mention}` is not a valid user!")
      end
      begin
        event.bot.ignore_user(user)
      rescue
        event.respond("❌ `#{mention}` is not a valid user!")
      end
      event.respond("✅ #{user.mention} has been temporarily ignored!")
    end

    command(:unignore, description: 'Unignores a given user', min_args: 1, max_args: 1) do |event, mention|
      event.channel.start_typing
      unless Helper.has_role?(event, [:owner, :dev, :bot])
        event.respond("❌ You don't have permission for that!")
        break
      end
      user = begin
        event.bot.parse_mention(mention)
      rescue
        event.respond("❌ `#{mention}` is not a valid user!")
      end
      begin
        event.bot.unignore_user(user)
      rescue
        event.respond("❌ `#{mention}` is not a valid user!")
      end
      event.respond("✅ #{user.mention} has been removed from the ignore list!")
    end

    command(:status, description: 'Set the bot as idle or dnd or invisible status. Admin only.', min_args: 1, max_args: 1) do |event, status|
      unless Helper.has_role?(event, [:owner, :dev, :bot])
        event.respond("❌ You don't have permission for that!")
        break
      end
      if status == 'idle'
        event.bot.idle
        event.respond('✅ Status set to **Idle**!')
      elsif status == 'dnd'
        event.bot.dnd
        event.respond('✅ Status set to **Do No Disturb**!')
      elsif status == 'online'
        event.bot.online
        event.respond('✅ Status set to **Online**!')
      elsif status == 'invisible' || status == 'offline'
        event.bot.invisible
        event.respond('✅ Status set to **Invisible**!')
      else
        event.respond('Enter a valid argument!')
      end
    end

    command(:shutdown, description: 'Shuts down the bot. Admin only.', usage: '&shutdown') do |event|
      puts "#{event.author.distinct}: \`#{event.message.content}\`"
      unless Helper.is_bot_owner?(event.user)
        event.respond("❌ You don't have permission for that!")
        break
      end
      message = event.respond 'Saving and exiting... '
      Helper.save_all
      event.bot.invisible
      message.edit('All saved. Goodbye!')
      Helper.quit
    end

    command(:eval, description: 'Evaluate a Ruby command. Admin only.', usage: "#{Config.prefix}eval code") do |event, *code|
      unless Helper.has_role?(event, [:owner])
        event.respond("❌ You don't have permission for that!")
        break
      end

      eval_message = code.join(' ')
      begin
        # Set eval result for further tracking later
        event.respond(eval eval_message)
        eval_message = nil
      rescue Discordrb::Errors::MessageTooLong
        # Determine how many characters the message is over
        lengthOver = eval_message.length - 2000
        event.respond("❌ Message was too long to send by #{lengthOver} characters!")
        break
      rescue => error
        # Exception:
        # stacktrace
        error_response = "#{$!}\n#{error.backtrace.join("\n")}"
        event.respond("```#{error_response}```")
        # Log to console as well
        puts error_response.to_s
        break
      end
    end

    command(:eval2, description: 'Evaluate a Ruby command. Admin only.', usage: "#{Config.prefix}eval code") do |event, *args|
      unless Helper.has_role?(event, [:owner])
        begin
          result = eval args.join(' ')
          if result.length >= 1984
            puts result
            event << "⚠ Your output exceeded the character limit! (`#{result.length - 1984}`/`1984`)"
            event << 'The result has been logged to the terminal instead :3'
          else
            event << ((result.nil? || result == '' || result == ' ' || result == "\n") ? "✅ Done! (No output)" : "Output: ```\n#{result}```")
          end
        rescue Exception => e
          event.respond(":x: An error has occured!! ```ruby\n#{e}```")
        end
      end
    end


    command(:bash, description: 'Evaluate a Bash command. Admin only. Use with care.', usage: '&bash code') do |event, *code|
      unless Helper.has_role?(event, [:owner])
        event.respond("❌ You don't have permission for that!")
        break
      end
      bashcode = code.join(' ')
      # Capture all output, including STDERR.
      to_be_run = "#{bashcode} 2>&1"
      result = ` #{to_be_run} `
      event << if result.nil? || result == '' || result == ' ' || result == "\n"
                 "✅ Done! (No output)"
               else
                 "Output: ```\n#{result}```"
               end
    end

    command(:dump, description: 'Dumps a selected channel. Admin only.', usage: '&dump [id]') do |event, channel_id|
      unless Helper.has_role?(event, [:owner, :dev, :bot])
        event << "❌ You don't have permission for that!"
        break
      end
      channel_id = event.channel.id if channel_id.nil?
      channel = begin
        event.bot.channel(channel_id)
      rescue
        event.respond('❌ Enter a valid channel id!')
      end
      output_filename = Helper.dump_channel(channel, event.channel, Config.dump_dir, event.message.timestamp)
      event.channel.send_file File.new([output_filename].sample)
    end

    # Migrated from the old Commands module
    command(:owners) do |event|
      event << 'This bot instance is managed/owned by the following users. Please contact them for any issues.'
      Config.bot_owners.each { |x| event << "`#{event.bot.user(x).distinct}`" }
      nil
    end

    command(:about, min_args: 0, max_args: 0) do |event|
      event << "`#{event.bot.user(event.bot.profile.id).distinct}` running **RC24-Bot v1-#{`git rev-parse --short HEAD`}** \n**https://github.com/Seriell/RC24-Bot **"
    end

    command(:prune, required_permissions: [:manage_messages], max_args: 1) do |event, num|
      Helper.ignore_bots(event)
      begin
        num = 50 if num.nil?
        count = 0
        event.channel.history(num).each do |x|
          if x.author.id == event.bot.profile.id
            x.delete
            count += 1
          end
        end
        message = event.respond("✅ Pruned #{count} messages!")
        sleep(10)
        message.delete
        event.message.delete
      rescue Discordrb::Errors::NoPermission
        event.channel.send_message("❌ I don't have permission to delete messages!")
        puts 'The bot does not have the delete message permission!'
      end
    end

    command(:pruneuser, required_permissions: [:manage_messages], max_args: 1) do |event, user, num|
      Helper.ignore_bots(event)
      begin
        user = event.bot.parse_mention(user)
        num = 50 if num.nil?
        count = 0
        event.channel.history(num).each do |x|
          if x.author.id == user.id
            x.delete
            count += 1
          end
        end
        message = event.respond("✅ Pruned #{count} messages!")
        sleep(10)
        message.delete
        event.message.delete
      rescue Discordrb::Errors::NoPermission
        event.channel.send_message("❌ I don't have permission to delete messages!")
        puts 'The bot does not have the delete message permission!'
      end
    end
  end
end
