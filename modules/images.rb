module SerieBot
	module Images
		extend Discordrb::Commands::CommandContainer
		class << self
			attr_accessor :folderimage_commands
		end
			@folderimage_commands = {
				#:name => 'path/to/folder'
			}

			@folderimage_commands.each { | name, folder |
				command(name, max_args: 0) do |event|
					# Check if folder exists, and has images.
					if not File.exists?(folder)
						# It doesn't exist, so let the user know that.
						event.respond("❌ The images folder for **#{name}** does not exist!")
						return
					elsif (Dir.entries(folder) - %w{ . .. }).empty?
						# Nothing's in the directory, so let the user know that.
						event.respond("❌ There are no images in the folder for **#{name}**!")
						return
					end
					# Shows typing indicator so that the user knows something is happening
					event.channel.start_typing

					files = Array.new
					# Select everything from the folder that is a file
					files = Dir[folder + '/*'].select { |e| File.file?(e) }
					file = files.sample

					puts "Selected file \"#{file}\" for command '#{name}'."
					event.channel.send_file File.new(["#{file}"].sample)
				end

			}
	end
end
