module SerieBot
  module EULA
    class << self
      attr_accessor :rules
    end

    extend Discordrb::Commands::CommandContainer

    def self.create_rules(bot)
      channel = Helper.channel_from_name(bot.server(Config.root_server), 'about')
      @rules[:about_id] = channel.id

      to_send = self.gen_rules_layout

      message = channel.send_message(to_send)
      @rules[:message_id] = message.id
      Helper.save_all
    end

    def self.gen_rules_layout
      to_send = "**__Server Rules__**\n"
      count = 1
      @rules[:actual_rules].each do |rule|
        to_send += "**#{count}.** #{rule}\n"
        count += 1
      end
      to_send += "\n**We’ll warn, kick or ban you if you break the above rules, depending on the severity of them. These rules are subject to change at any time, possibly without warning.**"
      return to_send
    end

    command(:editrule) do |event, rule_num, *text|
      unless event.server.id == Config.root_server
        event.respond('❌ Nice try.')
        break
      end
      unless Helper.has_role?(event, [:owner, :dev, :bot, :adm])
        event.respond("❌ You don't have permission for that!")
        break
      end
      unless rule_num.to_i.to_s == rule_num
        event.respond('❌ Please enter a valid rule number!')
        break
      end

      rule_text = text.join(' ')

      # Go back one since rules index off 1 and arrays 0
      @rules[:actual_rules][rule_num.to_i - 1] = rule_text
      Helper.save_all
      event.bot.channel(@rules[:about_id]).message(@rules[:message_id]).edit(self.gen_rules_layout)
      event.respond('✅ Hopefully updated!')
    end

    command(:rule) do |event, rule_num|
      unless event.server.id == Config.root_server
        event.respond("ℹ Please note that these aren't this server's rules.")
      end
      unless rule_num.to_i.to_s == rule_num
        event.respond('❌ Please enter a valid rule number!')
        break
      end

      number = rule_num.to_i - 1
      # Go back on per above
      if number == 33
        event.respond('( ͡° ͜ʖ ͡°)')
        break
      end
      rule_text = @rules[:actual_rules][number]
      if rule_text.nil?
        event.respond('❌ Please enter a valid rule number!')
        break
      end
      event.respond(rule_text)
    end
  end
end