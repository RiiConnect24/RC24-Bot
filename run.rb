require_relative 'modules/settings.rb'
module SerieBot
  require 'discordrb'
  require 'yaml'
  require 'fileutils'

  # Load helper as it is needed first.
  helper_path = 'modules/helper.rb'
  require_relative helper_path
  puts "Loaded: #{helper_path}"

  # Require other modules asides from Helper
  Dir['modules/*.rb'].each { |r| require_relative r unless r == helper_path; puts "Loaded: #{r}" }

  # List of modules to include
  modules = [
    Admin,
    Birthdays,
    Help,
    Logging,
    Utility,
    Mod,
    Codes,
    Commands
  ]
  # Set up bot
  if Config.appid == 0 || Config.appid.nil?
    puts 'You need to set your app ID in config.rb!'
    exit
  end

  bot = Discordrb::Commands::CommandBot.new token: Config.token, client_id: Config.appid, prefix: Config.prefix, parse_self: true, type: :bot
  modules.each { |m| bot.include! m; puts "Included: #{m}" }

  # Check if we should enable Morpher.
  if Config.morpher_enabled
    bot.include! Morpher
    puts 'Morpher enabled!'
  end

  # Load config files
  Helper.load_all
  
  # We should have settings loaded at this point
  

  # Run Bot
  Config.invite_url = bot.invite_url if Config.invite_url.nil?
  puts "Invite URL #{Config.invite_url}"

  bot.run :async
  bot.online
  bot.game = Config.playing

  thread = Thread.new { Birthdays.sleeping_beauty(bot) }
  thread.run
  bot.sync
end
