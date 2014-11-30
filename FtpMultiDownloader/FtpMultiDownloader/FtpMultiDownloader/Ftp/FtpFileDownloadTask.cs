using System;
using System.IO;
using System.Net;
using System.Net.FtpClient;
using System.Threading;
using FtpMultiDownloader.Conf;
using log4net;

namespace FtpMultiDownloader.Ftp
{
	public class FtpFileDownloadTask
	{
		private static readonly ILog Logger = LogManager.GetLogger(typeof(FtpFileDownloadTask));

		private readonly ConnectionInformation _source;
		private readonly string _fileName;
		private readonly string _destination;
		private readonly CountdownEvent _resetEvent;

		/// <summary>
		/// Initialize constructor
		/// </summary>
		public FtpFileDownloadTask(ConnectionInformation source, string fileName, CountdownEvent resetEventEvent)
		{
			_source = source;
			_fileName = fileName;
			_destination = Path.Combine(source.DestinationFolder, _fileName);
			_resetEvent = resetEventEvent;
		}

		/// <summary>
		/// Start file download
		/// </summary>
		public void DownloadFile()
		{
			if (_source == null)
			{
				throw new ArgumentException("source is null!");
			}
			if (_destination == null)
			{
				throw new ArgumentException("_destination is null!");
			}

			var conn = new FtpClient
				{
					Host = _source.Url, 
					Credentials = new NetworkCredential(_source.Username, _source.Password),
					EnableThreadSafeDataConnections = true
				};


			conn.BeginOpenRead(_source.Filepath + "/" + _fileName, DownloadfileCallback, conn);                
        }

		/// <summary>
		/// Execute download
		/// </summary>
        private async void DownloadfileCallback(IAsyncResult ar) {
            var conn = ar.AsyncState as FtpClient;

            try
            {
	            if (conn == null)
	            {
		            throw new InvalidOperationException("connection is null!");
	            }

	            // Delete file if exists
	            if (File.Exists(_destination))
	            {
		            File.Delete(_destination);
	            }

	            // Start download and write file
	            using (var filestream = new FileStream(_destination, FileMode.CreateNew, FileAccess.ReadWrite))
	            {
		            using (var istream = conn.EndOpenRead(ar))
		            {
			            await istream.CopyToAsync(filestream).ContinueWith(task => _resetEvent.Signal());
		            }
	            }
            }
            catch (Exception ex)
            {
	            Logger.ErrorFormat("Exception during filedownload. Url: {0}, Filepath: {1}, Filename: {2}, Destination: {3}", _source.Url, _source.Filepath, _fileName, _destination);
	            Logger.Error(ex);
            }
        }
	}
}
