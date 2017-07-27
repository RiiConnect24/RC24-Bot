# frozen_string_literal: true

module SerieBot
  # Parser and patcher for nwc24msg configs.
  module MailParse
    require 'bindata'
    # File layout for nwc24msg configs
    class Config < BinData::Record
      endian :big
      string :magic, read_length: 4, assert: 'WcCf'
      uint32 :version
      uint64 :wii_code
      uint32 :id_generation
      uint32 :has_registered
      string :wii_email_domain, read_length: 0x40
      string :passwd, read_length: 0x20
      string :mlchkid, read_length: 0x24
      array :points, initial_length: 5 do
        string :url, read_length: 0x80
      end
      string :reserved, read_length: 0xdc
      uint32 :title_booting
      # We won't even need this later on
      string :checksum, read_length: 0x4
    end


    def self.string_with_null(string, length)
      # Get amount of nulls needed for complete string
      needed_nulls = length - string.length
      return string + ("\x00" * needed_nulls)
    end

    def self.convert_mail(downloaded_cfg_path)
      begin
        config_io = File.open(downloaded_cfg_path)
      rescue IOError
        return 2
      end

      begin
        cfg = Config.read(config_io)
      rescue ValidityError
        return 3
      end

      # Patch domain
      cfg.wii_email_domain.assign(string_with_null('@rc24.xyz', 0x40))

      base_domain = 'http://mtw.rc24.xyz/cgi-bin/'
      # List of cgis (in order!)
      cgi_script = %w(account check receive delete send)
      cgi_script.length.times do |point_number|
        # Set point number to cgi script at point
        cfg.points[point_number].assign(string_with_null(base_domain + cgi_script[point_number] + '.cgi', 0x80))
      end

      # Remove current checksum
      to_checksum = cfg.to_hex[0...(cfg.to_hex.length - 8)]

      # To describe the checksum:
      # Take your entire file and break it into 4 byte groups.
      # Add them all up, and grab the lower 32 bits.

      # We currently have it out in hex, so we
      # can just split it 8 char groups
      # (since 2 hex chars = 1 byte)
      blocks = to_checksum.scan(/(........)/)
      checksum = 0
      current_number = BinData::Uint32be.new
      blocks.each do |block|
        # Convert back to string
        bytes = block[0].scan(/../).map { |x| x.hex.chr }.join

        current_number.clear
        current_number.read(bytes)
        checksum += current_number
      end
      # Get lower 32 bits
      # Many thanks to AwesomeMarioFan for helping me out with this
      checksum_final = checksum & 0xFFFFFFFF
      cfg.checksum.assign(BinData::Uint32be.new(checksum_final).to_binary_s)

      File.open(downloaded_cfg_path, 'wb') do |converted_file|
        begin
          cfg.write(converted_file)
        rescue IOError
          return 4
        end
      end
      # Success!
      1
    end
  end
end
