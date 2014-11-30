using System;
using System.Linq;
using System.Net;
using System.Net.FtpClient;
using System.Threading;
using FtpMultiDownloader.Conf;
using log4net;

namespace FtpMultiDownloader.Ftp
{
	public class FtpDownloadManager
	{
		private static readonly ILog Logger = LogManager.GetLogger(typeof(FtpDownloadManager));

		public ConnectionInformation Connection { get; set; }
		public ManualResetEvent ResetEvent { get; set; }
		private FtpListItem[] FilesToDownload { get; set; }

		/// <summary>
		/// Start download manager for one connection
		/// </summary>
		public void Start()
		{
			GetFileList();
			
			// start file downloads
			var cde = new CountdownEvent(FilesToDownload.Length);
			foreach (var task in FilesToDownload.Select(ftpListItem => new FtpFileDownloadTask(Connection, ftpListItem.Name, cde)))
			{
				task.DownloadFile();
			}

			cde.Wait();
			ResetEvent.Set();
		}

		/// <summary>
		/// Get list of files to download
		/// </summary>
		private void GetFileList()
		{
			ResetEvent.Reset();
			var conn = new FtpClient
			{
				Host = Connection.Url,
				Credentials = new NetworkCredential(Connection.Username, Connection.Password),
				EnableThreadSafeDataConnections = true
			};

			try
			{
				conn.Connect();
				FilesToDownload = conn.GetListing(Connection.Filepath, FtpListOption.AllFiles).Where(c => c.Type == FtpFileSystemObjectType.File).ToArray();
			}
			catch (Exception ex)
			{
				Logger.Error("Get file list failed", ex);
			}
			finally
			{
				try
				{
					conn.Disconnect();
				}
				catch (Exception e)
				{
					Logger.Error("Cannot close connection. ", e);
				}
			}
		}
	}
}