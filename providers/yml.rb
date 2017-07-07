module SerieBot
  module Provider
    require 'yaml'

    class << self
      attr_accessor :full_codes
    end

    def self.load_codes
      path_to_yml = 'data/codes.yml'
      FileUtils.mkdir('data') unless File.exist?('data')
      unless File.exist?(path_to_yml)
        File.open(path_to_yml, 'w') { |file| file.write({:version=>1}.to_yaml) }
      end
      @full_codes = YAML.load(File.read(path_to_yml))
    end

    def self.save_codes
      merged = @full_codes.merge(Codes.codes)
      File.open('data/codes.yml', 'w+') do |f|
        f.write(merged.to_yaml)
      end
      @full_codes = merged
    end

    def self.get_user_code(ids)
      returned_codes = {}
      ids.each do |id|
        returned_codes[id.to_i] = @full_codes[id]
      end
      return returned_codes
    end
  end
end