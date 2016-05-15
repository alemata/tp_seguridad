require 'sinatra'

set :bind, '0.0.0.0'

post '/images' do
  client_id = params["client_id"]
  user_dir = FileUtils.mkdir_p("./images/#{client_id}")[0]
  file_dir = File.dirname(params["file_path"])
  new_file_dir = FileUtils.mkdir_p("#{user_dir}/#{file_dir}")[0]
  src_file_path = params["profile_picture"][:tempfile].path
  dst_file_path = "#{new_file_dir}/#{params["profile_picture"][:filename]}"
  FileUtils.cp(src_file_path, dst_file_path)
end
