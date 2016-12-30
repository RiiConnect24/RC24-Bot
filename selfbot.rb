require 'discordrb'

#Make a Bot object
bot = Discordrb::Bot.new(token: 'MjI4NTc0ODIxNTkwNDk5MzI5.Cyilow.8o9xWiHlIpa8D6G1Ddv0gdB2jFU', type: :user, parse_self: true ) #Change type to :bot if using a bot

#bot.pm do |event|
#  event.user.pm('`Hello from the selfbot`')
#en

#bot.message do |event|
##	break if event.user.id != bot.profile.id#'#
#	event.message.edit("#{event.message} | I like :cookie:")


#end
bot.message(with_text: "/shrug") do |event, id|
	break if event.user.id != bot.profile.id
	event.message.delete
	event.respond('¯\_(ツ)_/¯')
end


#end
bot.message(with_text: "/idletest") do |event, id|
	break if event.user.id != 228574821590499329
	bot.profile.status=:idle
end
bot.message(with_text: "/offlinetest") do |event, id|
	break if event.user.id != 228574821590499329
	bot.profile.status=:offline
end

bot.message(with_text: "/lenny") do |event, id|
	break if event.user.id != bot.profile.id
	event.message.delete
	event.respond('( ͡° ͜ʖ ͡°)')
end

bot.message(start_with: "/owner ") do |event|
    break if event.user.id != bot.profile.id

    id = event.message.content.slice!(7, event.message.content.size)
				owner = event.bot.server(id).owner
			event.respond(":bust_in_silhouette: Owner of server `#{event.bot.server(id).name}` is **#{owner.distinct}** | #{owner.mention}")
end

bot.message(start_with: "/eval ") do |event|
	puts event.message.content
    break if event.user.id != 228574821590499329
    event.respond(eval(event.message.content.slice!(6, event.message.content.size)))
end

bot.message(start_with: "/reminder ") do |event|
    break if event.user.id != 228574821590499329
    time = event.message.content.slice!(10, event.message.content.size)
    event.respond("`Reminder set for #{time} seconds(s) from now!")
    sleep(time.to_i)
    event.respond("#{event.author.mention}! This is your reminder!")
end

bot.message(start_with: "/game ") do |event|
    break if event.user.id != 228574821590499329
	game = event.message.content.slice!(6, event.message.content.size)
    bot.game = game
	event.respond("`[auto]` Game set to `#{game}`!")
end

bot.message(start_with: "/hide ") do |event|
    break if event.user.id != bot.profile.id
    event.message.delete
end

bot.message(start_with: "/dump") do |event, id|
	break if event.user.id != 228574821590499329
	channel_id = event.message.content.slice!(6, event.message.content.size)

	if channel_id.nil?
		channel_id = event.channel.id
	end
	channel = bot.channel(channel_id)

	if channel.private?
		server = "DMs"
	else
		server = channel.server.name
	end

	event.respond("`Dumping messages from channel \"#{channel.name.gsub("`", "\\`")}\" in #{server.gsub("`", "\\`")}, please wait...`")
	if !(channel.private?)
		output_filename = "output_" + server + "_" + channel.server.id.to_s + "_" + channel.name + "_" + channel.id.to_s + "_" + event.message.timestamp.to_s + ".txt"
	else
		output_filename = "output_" + server + "_" + channel.name + "_" + channel.id.to_s + "_" + event.message.timestamp.to_s + ".txt"
	end
	output_filename = output_filename.gsub(" ","_").gsub("+","").gsub("\\","").gsub("/","").gsub(":","").gsub("*","").gsub("?","").gsub("\"","").gsub("<","").gsub(">","").gsub("|","")
	hist_count_and_messages = [Array.new, [0, Array.new]]

	output_file = File.open(output_filename, 'w')

	def parse_history(hist, count)
		messages = Array.new
		i = 0
		until i == hist.length
			message = hist[i]
			if message == nil
				puts "nii"
				break
			end
			if message.author.nil?
				author = "Unknown Disconnected User"
			else
				author = message.author.distinct
			end
			time = message.timestamp
			content = message.content

			attachments = message.attachments

			messages[i] = "--#{time} #{author}: #{content}"
			messages[i] += "\n<Attachments: #{attachments[0].filename}: #{attachments[0].url}}>" unless attachments.empty?
			i += 1

			count += 1
		end
		return_value = [count, messages]
		return return_value
	end
	offset_id = channel.history(1,1,1)[0].id

	while true
		hist_count_and_messages[0] = channel.history(100, nil, offset_id)
		break if hist_count_and_messages[0] == []
		hist_count_and_messages[1] = parse_history(hist_count_and_messages[0], hist_count_and_messages[1][0])
		output_file.write((hist_count_and_messages[1][1].reverse.join("\n") + "\n").encode("UTF-8")) #write to file right away, don't store everything in memory
		output_file.flush
		offset_id = hist_count_and_messages[0][0].id
	end
	output_file.close
	puts "Uploading output..."
	event.respond("`#{hist_count_and_messages[1][0]} messages logged.`")
	event.channel.send_file File.new([output_filename].sample)
	puts "Done. Dump file: #{output_filename}"
end
bot.message(start_with: "/delete_all_messages") do |event|
    break if event.user.id != bot.profile.id

    channel_id = event.channel.id
    channel = bot.channel(channel_id)
    server = channel.server.name
    hist_and_count = [Array.new, 0]

    def parse_history(hist, count, author)
        i = 0
        until i == hist.length
            i += 1
			message = hist[i]
			if message.author.id == author.id
				message.delete
			end

            count += 1
        end
        return count
    end
    offset_id = channel.history(1,1,1)[0].id

    while true
        hist_and_count[0] = channel.history(100, nil, offset_id) # next 100
        break if hist_and_count[0] == []
        hist_and_count[1] = parse_history(hist_and_count[0], hist_and_count[1], event.author)
        offset_id = hist_and_count[0][0].id
    end
    event.respond("`#{hist_and_count[1]} messages counted on channel \"#{channel.name.gsub("`", "\\`")}\" in #{server.gsub("`", "\\`")}.`")
    puts "Channel ID #{channel.id}: #{hist_and_count[1]} messages counted."
end

bot.message(start_with: "/count ") do |event|
    #Only I can use this command okay.
    break if event.user.id != bot.profile.id
    channel_id = event.message.content.slice!(7, event.message.content.size)

    if channel_id.nil?
        channel_id = event.channel.id
    end
    channel = bot.channel(channel_id)

    if channel.private?
        server = "DMs"
    else
        server = channel.server.name
    end
    event.respond("`Counting messages from channel \"#{channel.name.gsub("`", "\\`")}\" in #{server.gsub("`", "\\`")}, please wait...`")
    hist_and_count = [Array.new, 0]

    #Parse History
    def parse_history(hist, count)
#        puts "woof #{hist.length}"
        i = 0
        until i == hist.length
            if hist[i] == nil
                puts "nii"
                break
            end
            i += 1

            count += 1
        end
        return count
    end
    offset_id = channel.history(1,1,1)[0].id

    #Now let's count!
    while true
        hist_and_count[0] = channel.history(100, nil, offset_id) # next 100
        break if hist_and_count[0] == []
        hist_and_count[1] = parse_history(hist_and_count[0], hist_and_count[1])
        offset_id = hist_and_count[0][0].id
    end
    event.respond("`#{hist_and_count[1]} messages counted on channel \"#{channel.name.gsub("`", "\\`")}\" in #{server.gsub("`", "\\`")}.`")
    puts "Channel ID #{channel.id}: #{hist_and_count[1]} messages counted."
end
#Actually run the goddamn bot.
bot.message(start_with: "/sdump") do |event, id|
	break if event.user.id != bot.profile.id
	channel_id = event.message.content.slice!(7, event.message.content.size)

	if channel_id.nil?
		channel_id = event.channel.id
	end
	channel = bot.channel(channel_id)

	if channel.private?
		server = "DMs"
	else
		server = channel.server.name
	end

	event.respond("`Silently dumping messages from channel \"#{channel.name.gsub("`", "\\`")}\" in #{server.gsub("`", "\\`")}, please wait...`")
	if !(channel.private?)
		output_filename = "output_" + server + "_" + channel.server.id.to_s + "_" + channel.name + "_" + channel.id.to_s + "_" + event.message.timestamp.to_s + ".txt"
	else
		output_filename = "output_" + server + "_" + channel.name + "_" + channel.id.to_s + "_" + event.message.timestamp.to_s + ".txt"
	end
	output_filename = output_filename.gsub(" ","_").gsub("+","").gsub("\\","").gsub("/","").gsub(":","").gsub("*","").gsub("?","").gsub("\"","").gsub("<","").gsub(">","").gsub("|","")
	hist_count_and_messages = [Array.new, [0, Array.new]]

	output_file = File.open(output_filename, 'w')

	def parse_history(hist, count)
		messages = Array.new
		i = 0
		until i == hist.length
			message = hist[i]
			if message == nil
				puts "nii"
				break
			end
			if message.author.nil?
				author = "Unknown Disconnected User"
			else
				author = message.author.distinct
			end
			time = message.timestamp
			content = message.content

			attachments = message.attachments

			messages[i] = "--#{time} #{author}: #{content}"
			messages[i] += "\n<Attachments: #{attachments[0].filename}: #{attachments[0].url}}>" unless attachments.empty?
			puts messages[i]
			i += 1

			count += 1
		end
		return_value = [count, messages]
		return return_value
	end
	offset_id = channel.history(1,1,1)[0].id

	while true
		hist_count_and_messages[0] = channel.history(100, nil, offset_id)
		break if hist_count_and_messages[0] == []
		hist_count_and_messages[1] = parse_history(hist_count_and_messages[0], hist_count_and_messages[1][0])
		output_file.write((hist_count_and_messages[1][1].reverse.join("\n") + "\n").encode("UTF-8")) #write to file right away, don't store everything in memory
		output_file.flush
		offset_id = hist_count_and_messages[0][0].id
	end
	output_file.close
	puts "Uploading output..."
	event.respond("`#{hist_count_and_messages[1][0]} messages silently logged.`")
	puts "Done. Dump file: #{output_filename}"
end


bot.run
