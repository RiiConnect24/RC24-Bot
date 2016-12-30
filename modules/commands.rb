module SerieBot
  module Commands
    extend Discordrb::Commands::CommandContainer
    class << self
			attr_accessor :image_commands
      attr_accessor :text_commands
		end

    command(:owners) do |event|
        event << "This bot instance is managed/owned by the following users. Please contact them for any issues."
        Config.bot_owners.each {|x| event << "`#{event.bot.user(x).distinct}`"}
        nil
    end

    #The folder the images are stored in.
    #For example, 'images' means files are stored as 'images/file.jpg'
    base_path = 'images'
    @image_commands = {
      # :name => 'path/to/file.png'
      # Supports any file types, files over ~8MB will fail.
    }


    @text_commands = {
    :facedesk => "https://giphy.com/gifs/XLOsdacfjL5cI",
  }

    # Import commands:

      @image_commands.each { | name, file |

        command(name, description: name) do |event|
          next if Config.blacklisted_channels.include?(event.channel.id) rescue nil
          event.channel.start_typing
          event.channel.send_file File.new(["#{base_path}/#{file}"].sample)
        end
        puts "Command #{Config.prefix}#{name} with image \"#{base_path + "/" + file}\" loaded successfully!"
      }

      @text_commands.each { | name, text |
        command(name, description: name) do |event|
          next if Config.blacklisted_channels.include?(event.channel.id) rescue nil
          event.channel.start_typing
          event.respond(text)
        end
        puts "Command #{Config.prefix}#{name} loaded successfully!"
      }

      command(:about, min_args: 0, max_args: 0) do |event|
        event << "`#{event.bot.user(event.bot.profile.id).distinct}` running **SerieBot-Git** \n**https://github.com/Seriell/Serie-Bot **"
      end
  end
end
