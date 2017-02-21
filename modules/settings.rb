module SerieBot
  module Config
    class << self
      # Bot important
      attr_accessor :token
      attr_accessor :appid
      attr_accessor :prefix
      attr_accessor :playing
      attr_accessor :bot_owners

      attr_accessor :invite_url

      #Status
      attr_accessor :status

      # Logging dump dir
      attr_accessor :dump_dir
      # Debug mode
      attr_accessor :debug

      attr_accessor :morpher_enabled

      # Ignore specific channels
      attr_accessor :ignore_ids

      # General settings (not in config.rb)
      attr_accessor :settings
    end

    # Load bot configuration
    config_location = Dir.pwd + '/config.rb'
    if File.exists?(config_location)
      require_relative config_location
    else
      puts "Could not load config.rb!\n"
      puts "If this is your first time running this bot, please copy config.rb.example to config.rb and edit as necessary.\n"
      exit
    end

    # Bot settings get loaded in Helper (at the very bottom of the file!)
  end
end
