module SerieBot
  module Config
  class << self
      attr_accessor :token
      attr_accessor :appid
      attr_accessor :prefix
      attr_accessor :playing
      attr_accessor :bot_owners

      attr_accessor :invite_url
      attr_accessor :blacklisted_channels

      #Cleverbot
      attr_accessor :use_cleverbot
      attr_accessor :cleverbot_api_user
      attr_accessor :cleverbot_api_token

      #Advanced Options
      attr_accessor :use_bundler

      #Status
      attr_accessor :status
      attr_accessor :streaming
      attr_accessor :twitch_url

      #Message logging
      attr_accessor :logging
      attr_accessor :ignored_servers

      attr_accessor :dump_dir
      attr_accessor :login_type
  end
    config_location = Dir.pwd + '/config.rb'
    if File.exists?(config_location)
      require_relative config_location
    else
      puts "Could not load config.rb!\n"
      puts "If this is your first time running this bot, please copy config.rb.example to config.rb and edit as necessary.\n"
      exit
    end
  end
end
