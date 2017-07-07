module SerieBot
 module Codes
   require 'yaml'
   extend Discordrb::Commands::CommandContainer
   extend Discordrb::EventContainer
   class << self
     attr_accessor :codes
   end

   def self.parse_args(args)
     # Remove first option
     args.delete_at(0)
     # Join em
     args = args.join(' ')
     # Grab the text inside
     args.sub!('| ', '')
     input = args.split(' | ')
     # Return code and name
     return input[1], input[0]
   end

   def self.modify_codes(user_id, args, option)
     # Save type of code before deletion
     type = args[0]
     # Populate if nil
     user_codes = @codes[user_id]
     user_codes[type.to_sym] = {} if @codes[user_id][type.to_sym].nil?
     type_codes = user_codes[type.to_sym]
     # Grab code and name from args
     code, name = parse_args(args)
     # Set status defaults
     to_say = name
     # If failure
     if code.nil? && option != 'remove'
      to_say = '❌ Please enter a valid code!'
     elsif name.nil?
      to_say = '❌ Please enter a valid name!'
     elsif option == 'add'
     # Set + save codes
     type_codes[name] = code
     to_say = "✅ Added a code for `#{name}`"
     elsif option == 'edit'
       # Populate if nil
       if type_codes[name].nil?
       to_say = "❌ A code for `#{name}` is not registered."
     else
       # Set to code
       type_codes[name] = code
       to_say = "✅ Edited the code for `#{name}`"
     end
     elsif option == 'remove'
       if type_codes[name].nil?
         to_say = "❌ A code for Wii `#{name}` is not registered."
       else
         type_codes.delete(name)
         # Check if type is empty
         if type_codes == {}
           # We can go ahead and delete this code.
           user_codes.delete(type.to_sym)
         end
         to_say = "✅ Deleted the code for `#{name}`"
       end
     end

     # Save anyway, it can't hurt.
     Provider.save_codes
     return to_say
   end

   command(:code) do |event, option, *args|
     Helper.ignore_bots(event)
     user_id = event.user.id
     # Load individually
     @codes = Provider.get_user_code([user_id])
     # Create code for the user, to prevent future issues
     Codes.codes[user_id] = {} if Codes.codes[user_id].nil?
     modification_options = %w(add edit remove)
     if modification_options.include? option
      case args[0]
        # Only allow these types
        when 'wii', '3ds', 'nnid', 'switch', 'game'
          # Send information off to function
          return_text = modify_codes(user_id, args, option)
          # Say response
          event.respond(return_text)
        else
          event << "❌ Please enter a valid argument for the option `#{option}`."
          event << 'Valid arguments: `wii`, `3ds`, `nnid`, `switch`, `game`.'
      end
     elsif option == 'lookup' || option == 'list'
       # Mention, search for, current user
        user = event.bot.parse_mention(args[0])
        user = event.bot.find_user(args[0])[0] if user.nil?
        user = event.user if args[0].nil?
        if user.nil?
          event.respond("❌ Could not find user #{args[0]}!")
          break
        end
        @codes = Provider.get_user_code([user_id, user.id])

        # Check if the user is on this server.
        begin
          user_name = user.on(event.server).display_name
        rescue NoMethodError
          user_name = user.name
          # They may not have even used the bot, so make sure.
          @codes[user.id] = {} if @codes[user.id].nil?
        end

        # Make sure they have friend codes, period.
        if @codes[user.id].nil? || codes[user.id] == {}
          event.respond("❌ **#{user_name}** has not added any friend codes!")
          break
        else
          embed_sent = Discordrb::Webhooks::Embed.new
          # Start out with a line return due to embed author later on
          code_types = {
            :wii => '<:Wii:259081748007223296> **Wii**',
            '3ds'.to_sym => '<:New3DSXL:287651327763283968> **3DS**',
            :nnid => '<:NintendoNetworkID:287655797104836608> **Nintendo Network ID**',
            :switch => '<:Switch:287652338791874560> **Switch**',
            :game => '🎮 **Games**'
          }
          badge_types = {
            :owner => '<:BadgeBotDev:331597705472114688>',
            :dev => '<:BadgeDeveloper:329710752778944512>',
            :adm => '<:BadgeAdmin:329734061532774403>',
            :mod => '<:BadgeModerator:329715070768513024>',
            :hlp => '<:BadgeHelper:329722382790950912>',
            :don => '<:BadgeDonator:329712167983251458>',
            :trn => '<:BadgeTranslator:329723303814234113>'
          }

          code_types.each do |type, title|
            unless @codes[user.id][type].nil?
              column_codes = @codes[user.id][type].each_slice(2).to_a

              column_codes.each do |column|
                field_text = ''
                column.each do |name, code|
                  code_output = code
                  field_text += "#{name}:\n`#{code_output}`\n"
                end
                embed_sent.add_field(name: title, value: field_text, inline: true)
              end
            end
          end

          badges_list = ''
          unless event.channel.private? || !user.is_a?(Discordrb::Member)
            badge_types.each do |type|
              # First element in array is role type
              if Helper.has_role?(event, [type[0]])
                # Next element in array is emoji
                badges_list += type[1] + ' '
              end
            end
          end
          if event.channel.private?
           badges_list = "Sorry, you can't view badges in DMs."
          end
          unless badges_list == ''
            embed_sent.add_field(name: '🏅**Badges**', value: badges_list)
          end
          # 33762 is the same as hex #0083e2
          embed_sent.colour = Helper.color_from_user(user, event.channel, '0083e2')
          embed_sent.author = Discordrb::Webhooks::EmbedAuthor.new(name: "Profile for #{user_name}",
                                       url: nil,
                                       icon_url: Helper.avatar_url(user, 32))
          event.channel.send_embed('', embed_sent)
        end
      elsif option == 'help'
        event.respond(Helper.get_help)
      else
        event << '❌ Please enter a valid option for the command.'
        event << 'Valid options: `add`, `edit`, `remove`, `lookup`, `enable`, `disable`.'
      end
    end

    command(:add, min_args: 1, max_args: 1) do |event, mention|
      Helper.ignore_bots(event)
      user = begin
             event.bot.parse_mention(mention)
           rescue
             event.respond('❌ Enter a valid user!')
             break
      end
      # Check if the user is on this server.
      begin
        user_name = user.on(event.server).display_name
      rescue NoMethodError
        user_name = user.display_name
        # They may not have even used the bot, so make sure.
        @codes[user.id] = {} if @codes[user.id].nil?
      end
      if user.id == event.user.id
        event.respond("❌ You can't add yourself!")
        break
      end
      if @codes[user.id].nil? || @codes[user.id][:wii].nil?
        event.respond("❌ **#{user.on(event.server).display_name}** has not added any Wii friend codes! Keep in mind this command currently only works with Wii codes.")
        break
      end
      if @codes[event.user.id].nil?
        event.respond('❌ You have not added any Wii friend codes! (Currently, this command only works with Wii codes.)')
        break
      end
      event << "**You have requested to add  #{user.on(event.server).display_name}'s Wii.**\n"
      if !@codes[user.id][:wii].nil?
        event << '<:Wii:259081748007223296> **Wiis**:'
        @codes[user.id][:wii].each do |wii, code| #
          code_output = code
          event << "`#{code_output}` - #{wii}"
        end
        nil
        event << ''
        message = ''
        message << "#{event.user.name} has requested to add your Wii's friend code!\nTheir codes:\n\n"
        @codes[event.user.id][:wii].each do |wii, code| #
          code_output = code
          message << "`#{code_output}` - #{wii}\n"
        end

        user.pm(message)
      else
        event << "❌ **#{user_name}** has not added any Wii codes!"
        next
      end
    end

    command(:wipecodes) do |event, *args|
      Helper.ignore_bots(event)
      unless Helper.has_role?(event, [:owner])
        event.respond("❌ You don't have permission for that!")
        break
      end
      user = begin
             event.bot.parse_mention(args[0])
           rescue
             event.user
           end
      user = event.user if args[0].nil?
      @codes = Provider.get_user_code([user.id])
      Codes.codes[user.id] = {}
      puts "#{event.user.distinct} has wiped #{user.distinct}'s codes."
      event << "Wiped all codes saved by `#{user.distinct}` (ID: #{user.id})"
      Provider.save_codes
    end

    command(:save) do |event|
      unless Helper.has_role?(event, [:owner, :dev, :bot])
        event.respond("❌ You don't have permission for that!")
        break
      end
      message = event.respond 'Saving...'
      Helper.save_all
      message.edit('All saved!')
    end
  end
end
