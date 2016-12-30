# encoding: UTF-8

module SerieBot
  module Zalgo
    extend Discordrb::Commands::CommandContainer
    extend Discordrb::EventContainer
    def self.zalgo(text, intensity=50)
      zalgo_chars = (0x0300..0x036F).map { |i| i.chr('UTF-8') }
      zalgo_chars.concat(["\u0488", "\u0489"])
      source = insert_randoms(text.upcase)
      zalgoized = []
      source.each_char do |letter|
        zalgoized << letter
        zalgo_num = rand(intensity)
        zalgo_num.times { zalgoized << zalgo_chars.sample }
      end
      zalgo_text = zalgoized.join(zalgo_chars.sample)
      return zalgo_text
    end

    private
    def self.insert_randoms(text)
      random_extras = (0x1D023..0x1D045).map { |i| i.chr('UTF-8') }
      newtext = []
      text.each_char do |char|
        newtext << char
        newtext << random_extras.sample if rand(10) == 1
      end
      return newtext.join
    end
  end
end
