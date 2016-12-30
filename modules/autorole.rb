module SerieBot
  module Autorole
      extend Discordrb::Commands::CommandContainer
      extend Discordrb::EventContainer

      member_join do |event|
        puts "TEST"
      end

  end
end
