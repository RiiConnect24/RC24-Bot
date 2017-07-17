module SerieBot
  module MailParse
    require 'bindata'
    class Config < BinData::Record
      endian :big
      string :magic, read_length: 4, assert: 'WcCf'
      uint32 :unknown
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
    def self.convert_mail(downloaded_cfg_path)
      begin
        io = File.open(downloaded_cfg_path)
      rescue IOError
        return 2
      end

      begin
        cfg = Config.read(io)
      rescue ValidityError
        return 3
      end

      # Patch domain
      original = cfg.wii_email_domain.to_binary_s
      replacement_url = original.delete("\x00")
      replacement_url = replacement_url.gsub('@wii.com', '@rc24.xyz')
      needed_nulls = 0x40 - replacement_url.length
      cfg.wii_email_domain.assign(replacement_url + ("\x00" * needed_nulls))

      cfg.points.each do |url|
        # Patch with our URL from original or other
        original = url.to_binary_s
        replacement_url = original.delete("\x00")
        replacement_url = replacement_url.gsub(/https?:\/\/(...).wc24.wii.com/, 'http://rc24.xyz')
        replacement_url = replacement_url.gsub(/https?:\/\/riiconnect24.net/, 'http://rc24.xyz')

        # Add nulls to create original length
        # 0x80 is the URL's max length, so create up to that
        needed_nulls = 0x80 - replacement_url.length
        url.assign(replacement_url + ("\x00" * needed_nulls))
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

      File.open(downloaded_cfg_path, 'wb') do |io|
        begin
          cfg.write(io)
        rescue IOError
          return 4
        end
      end
      # Success!
      1
    end
  end
end
