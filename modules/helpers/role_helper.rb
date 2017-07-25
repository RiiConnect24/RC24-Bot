# frozen_string_literal: true

module SerieBot
  # Role and config functions for the bot.
  module RoleHelper
    class << self
      attr_accessor :types
    end

    # Format:
    # [name, show_message]
    @types = {
      owner: ['Dummy Entry', true],
      dev: ['RiiConnect24 Developers', true],
      bot: ['Bot Helpers', false],
      mod: ['Server Moderators', true],
      hlp: ['Helpers', false],
      don: ['Donators', false],
      adm: ['Server Admins', false],
      trn: ['Translators', false]
    }

    # Gets the channel/role's ID based on the given parameters
    def self.get_xxx_id?(server_id, type, short_type)
      # Set all to defaults
      Config.settings[server_id] = {} if Config.settings[server_id].nil?
      Config.settings[server_id][type] = {} if Config.settings[server_id][type].nil?
      Config.settings[server_id][type][short_type]
    end

    # Saves the role's ID based on the given parameters
    # e.g save_xxx_id?('srv', 'channel', event.server.id, id)
    # type can be channel, role, etc
    # short_name: mod, dev, srv, etc
    def self.save_xxx_id?(server_id, type, short_name, id)
      if Config.debug
        puts "Saving short type #{type} (type #{short_name}) with ID #{id} for server ID #{server_id}"
      end
      # Potentially populate
      Config.settings[server_id] = {} if Config.settings[server_id].nil?
      Config.settings[server_id][type] = {} if Config.settings[server_id][type].nil?
      Config.settings[server_id][type][short_name] = id
      save_all
    end

    # Checks to see if the server has the needed channel, and if not deals accordingly to fix it.
    def self.xxx_channel?(event, short_type, channel_name)
      # Check if config already has a role
      xxx_channel_id = get_xxx_id?(event.server.id, 'channel', short_type)

      if xxx_channel_id.nil?
        # Set to default
        begin
          xxx_channel_id = channel_from_name(event.server, channel_name).id
          save_xxx_id?(event.server.id, 'channel', short_type, xxx_channel_id)
        rescue NoMethodError
          # Rip, we'll set the channel in config.
          # If we're debugging, might be helpful to say that.
          if Config.debug
            puts "I wasn't able to find the channel \"#{channel_name}\" for use with #{short_type}."
          end
          return nil
        end
        event.server.general_channel.send_message("Channel \"#{channel_name}\" set to default. Use `#{Config.prefix}config setchannel #{short_type} <channel name>` to change otherwise.")
      end

      # Check if the server has the specified channel
      event.bot.channel(xxx_channel_id).id
    end

    # Checks to see if the user has the given role, and if not deals accordingly to fix it.
    def self.xxx_role?(event, role_type, full_name, show_message = true, other_user = nil)
      # Check if config already has a role
      xxx_role_id = get_xxx_id?(event.server.id, 'role', role_type)

      if xxx_role_id.nil?
        # Set to default
        begin
          xxx_role_id = role_from_name(event.server, full_name).id
          save_xxx_id?(event.server.id, 'role', role_type, xxx_role_id)
        rescue NoMethodError
          if show_message
            event.respond("I wasn't able to find the role \"#{full_name}\" for role-related tasks! See `#{Config.prefix}config help` for information.")
          end
          return false
        end
        event.respond("Role \"#{full_name}\" set to default. Use `#{Config.prefix}config setrole #{role_type} <role name>` to change otherwise.")
      end
      # Check if the member has the ID of said role
      user = if other_user.nil?
               event.user
             else
               other_user
             end
      # Check if user can even do so
      begin
        user.roles
      rescue NoMethodError
        puts 'Looks like the user was not a member.' if Config.debug
        return false
      end
      event.respond("Looks like the #{full_name} role has a faulty ID! Please set the role again.") if xxx_role_id.nil?
      user.role?(event.server.role(xxx_role_id))
    end

    def self.named_role?(event, roles, user = nil)
      user = event.user if user.nil?

      # Only support listed types.
      roles.each do |role_type|
        if @types.include? role_type
          if role_type.to_s == 'owner'
            status = Config.bot_owners.include?(user.id)
          else
            # Normal users don't have roles, only Members, so we can't check.
            begin
              return false if user.roles.nil?
            rescue NoMethodError
              return false
            end
            role_info = @types[role_type]
            status = xxx_role?(event, role_type.to_s, role_type[0], role_info[1], user)
          end
          next unless status
          # They've got at least one of the roles
          puts "Looks like the user has #{role_type}" if Config.debug
          return status
        elsif Config.debug
          puts "I don't have the #{role_type} role in my list... perhaps you made a typo?"
        end
      end

      # If we got here we couldn't find the role
      puts 'The user had none of the roles requested!' if Config.debug
      false
    end

    # We have to specify user here because we're checking if another user is verified
    def self.other_verified?(event, other_user = nil)
      user = if other_user.nil?
               event.user
             else
               other_user
             end
      xxx_role?(event, 'vfd', 'Verified', true, user)
    end

    # TODO: perhaps save and stuff?
    def self.quit
      puts 'Exiting...'
      exit
    end

    def self.load_xyz(name, default_yaml = { version: 1 })
      folder = 'data'
      path_to_yml = "#{folder}/#{name}.yml"
      FileUtils.mkdir(folder) unless File.exist?(folder)
      unless File.exist?(path_to_yml)
        File.open(path_to_yml, 'w') { |file| file.write(default_yaml.to_yaml) }
      end
      YAML.load(File.read(path_to_yml))
    end

    def self.save_xyz(name, location)
      File.open("data/#{name}.yml", 'w+') do |f|
        f.write(location.to_yaml)
      end
    end

    def self.load_all
      Morpher.messages = load_xyz('morpher') if Config.morpher_enabled
      Codes.codes = load_xyz('codes')
      Logging.recorded_actions = load_xyz('actions', ban: {}, kick: {}, warn: {})
      BirthdayHandler.dates = load_xyz('birthdays')
      RuleHandler.rules = load_xyz('eula', actual_rules: {})
    end

    def self.save_all
      save_xyz('morpher', Morpher.messages)
      save_xyz('codes', Codes.codes)
      save_xyz('settings', Config.settings)
      save_xyz('birthdays', BirthdayHandler.dates)
      save_xyz('eula', RuleHandler.rules)
    end

    # We must keep this separate due to how everything is loaded.
    def self.load_settings
      folder = 'data'
      settings_path = "#{folder}/settings.yml"
      FileUtils.mkdir(folder) unless File.exist?(folder)
      unless File.exist?(settings_path)
        puts "[ERROR] I wasn't able to find data/settings.yml! Please grab the example from the repo."
      end
      Config.settings = YAML.safe_load(File.read(settings_path))
    end

    def self.help_text
      help = "**__Using the bot__**\n"
      help += "\n"
      help += "**Adding codes:**\n"
      help += "`#{Config.prefix}code add wii | Wii Name Goes here | 1234-5678-9012-3456`\n"
      help += "`#{Config.prefix}code add game | Game Name | 1234-5678-9012`\n"
      help += "and many more types! Run `#{Config.prefix}code add` to see all supported code types right now, such as the 3DS and Switch.\n"
      help += "\n"
      help += "**Editing codes**\n"
      help += "`#{Config.prefix}code edit wii | Wii Name | 1234-5678-9012-3456`\n"
      help += "`#{Config.prefix}code edit game | Game Name | 1234-5678-9012`\n"
      help += "\n"
      help += "**Removing codes**\n"
      help += "`#{Config.prefix}code remove wii | Wii Name`\n"
      help += "`#{Config.prefix}code remove game | Game Name`\n"
      help += "\n"
      help += "**Looking up codes**\n"
      help += "`#{Config.prefix}code lookup @user`\n"
      help += "\n"
      help += "**Adding a user's Wii**\n"
      help += "`#{Config.prefix}add @user`\n"
      help += 'This will send you their codes, and then DM them your Wii/game codes.'
      help
    end

    # Load settings for all.
    load_settings
  end
end
