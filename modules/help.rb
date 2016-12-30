module SerieBot
  module Help
    extend Discordrb::Commands::CommandContainer
    extend Discordrb::EventContainer

    command(:help, description: "Display a list of commands.") do |event|
      event.respond("**__Using the bot__**

**Adding codes:**
`!code add wii | Wii Name | 1234-5678-9012-3456` (You can add multiple Wiis with different names)
`!code add game | Game Name | 1234-5678-9012`

**Editing codes**
`!code edit wii | Wii Name | 1234-5678-9012-3456`
`!code edit game | Game Name | 1234-5678-9012`

**Removing codes**
`!code remove wii | Wii Name`
`!code remove game | Game Name`

**Looking up codes**
`!code lookup @User`

**Adding a user's Wii**
`!add @user`
This will show their codes, and they will be notified you wish to add them, and send them your codes.")

    end

  end
end
