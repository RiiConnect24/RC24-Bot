require_relative 'modules/settings.rb'
module SerieBot
    require 'discordrb'
    require 'yaml'
    require 'fileutils'

    def self.role(rolename, server)
        roles = server.roles
        roles.select { |r| r.name == rolename }.first
    end

    # Load helper as it is needed first.
    require_relative "modules/helper.rb"
    # Require other modules asides from Helper
    Dir['modules/*.rb'].each { |r| require_relative r unless r == "modules/helper.rb"; puts "Loaded: #{r}" }

    # List of modules to include
    modules = [
        Admin,
        Logging,
        Utility,
        Mod,
        Rii
    ]
    # Set up bot
    if Config.appid == 0 || Config.appid.nil?
        puts 'You need to set your app ID in config.rb!'
        exit
    end

    bot = Discordrb::Commands::CommandBot.new token: Config.token, client_id: Config.appid, prefix: Config.prefix, parse_self: true, type: Config.login_type
    modules.each { |m| bot.include! m; puts "Included: #{m}" }
    # Run Bot
    Config.invite_url = bot.invite_url if Config.invite_url.nil?
    puts "Invite URL #{Config.invite_url}"

    bot.run
    bot.online
end
