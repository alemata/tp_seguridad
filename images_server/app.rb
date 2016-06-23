require 'sinatra'
require 'webrick'
require 'webrick/https'
require 'openssl'

cert_name = [
  %w[CN localhost],
]

webrick_options = {
        :Host               => '0.0.0.0',
        :Port               => 4567,
        :Logger             => WEBrick::Log::new($stderr, WEBrick::Log::DEBUG),
        :DocumentRoot       => "./",
        :SSLEnable          => true,
        :SSLVerifyClient    => OpenSSL::SSL::VERIFY_NONE,
        :SSLCertName        => cert_name
}

class MyServer  < Sinatra::Base
  post '/files' do
    client_id = params["client_id"]
    user_dir = FileUtils.mkdir_p("./images/#{client_id}")[0]
    file_dir = File.dirname(params["file_path"])
    new_file_dir = FileUtils.mkdir_p("#{user_dir}/#{file_dir}")[0]
    src_file_path = params["file"][:tempfile].path
    dst_file_path = "#{new_file_dir}/#{params["file"][:filename]}"
    FileUtils.cp(src_file_path, dst_file_path)
  end
end

Rack::Handler::WEBrick.run MyServer, webrick_options


