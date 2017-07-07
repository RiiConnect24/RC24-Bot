module SerieBot
  module Provider
    require 'mysql2'
    require 'pry'
    require 'json'

    class << self
      attr_accessor :client
    end

    def self.load_codes
      data = Config.provider_options
      @client = Mysql2::Client.new(:host => data[:host], :username => data[:username], :password => data[:password], :database => data[:database])
      puts 'Logged in!'
      # We're not actually gonna load anything.
    end

    def self.save_codes
      Codes.codes.each do |id, user_data|
        # Get current codes
        query = @client.prepare('SELECT * from codes WHERE user_id=?')
        results = query.execute(id).first
        if results.nil?
          setup = @client.prepare('INSERT INTO codes (user_id) VALUES (?)')
          setup.execute(id.to_i)
          results = query.execute(id).first
        end
        # Loop through current table structure
        results.each do |name, data|
          puts '=========MySQL==========' if Config.debug
          next if name.to_s == 'user_id'
          next if user_data.nil?
          next if user_data[name.to_sym].nil?
          array_for_json = user_data[name.to_sym]
          array_for_json = {} if array_for_json.nil?
          escaped = @client.escape(array_for_json.to_json)
          puts "#{name.to_s}, #{escaped}, #{id.to_i}"
          # Match together current structure in codes array
          @client.query("UPDATE codes SET #{name} = '#{escaped}' WHERE user_id=#{id.to_i}")
          puts '========================' if Config.debug
        end
      end
    end

    def self.get_user_code(ids)
      query = @client.prepare('SELECT * from codes WHERE user_id=?')
      codes = {}
      ids.each do |id|
        stored_data = query.execute(id.to_i).first
        if stored_data.nil?
          setup = @client.prepare('INSERT INTO codes (user_id) VALUES (?)')
          setup.execute(id.to_i)
          codes[id] = {}
        else
          user_data = {}
          stored_data.each do |name, data|
            next if name == 'user_id'
            # Parse stored JSON data and set it to the related code array
            begin
              key_data = JSON.parse(data)
            rescue
              key_data = {}
            end
            next if key_data == {}
            user_data[name.to_sym] = key_data
          end
          codes[id] = user_data
        end
      end
      return codes
    end
  end
end