using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.FtpClient;
using System.Threading;
using log4net;

namespace FtpMultiDownloader.Ftp
{
	public class FtpMultiDownloadManager
	{
		private static readonly ILog Logger = LogManager.GetLogger(typeof(FtpMultiDownloadManager));

		private readonly Conf.FtpMultiDownloader _config;
		private readonly List<FtpDownloadManager> _downloadManagers = new List<FtpDownloadManager>();
		private WaitHandle[] _doneEvents;
		
		private bool _valid;
		
		/// <summary>
		/// Constructor
		/// </summary>
		public FtpMultiDownloadManager(Conf.FtpMultiDownloader config)
		{
			_config = config;
		}

		/// <summary>
		/// Validates configuration
		/// </summary>
		public bool Validate()
		{
			_valid = true;

			foreach (var connection in _config.Connections)
			{
				// validate ftp settings
				try
				{
					var ftpClient = new FtpClient
						{
							Host = connection.Url,
							Credentials = new NetworkCredential(connection.Username, connection.Password)
						};

					ftpClient.Connect();
					ftpClient.Disconnect();
				}
				catch (Exception e)
				{
					Logger.ErrorFormat("Validating ftp connection failed. Url: {0}, Filepath: {1}", connection.Url, connection.Filepath);
					Logger.Error(e);
					_valid = false;
				}

				// validate directory settings
				try
				{
					if (!Directory.Exists(connection.DestinationFolder))
					{
						Directory.CreateDirectory(connection.DestinationFolder);
					}

					var tmpFile = Path.Combine(connection.DestinationFolder, "test.txt");
					File.CreateText(tmpFile);
					File.Decrypt(tmpFile);
				}
				catch (Exception e)
				{
					Logger.ErrorFormat("Validating destination path failed. Folder: {0}", connection.DestinationFolder);
					Logger.Error(e);
					_valid = false;
				}
			}
			return _valid;
		}

		public bool Start()
		{
			if (_valid)
			{
				_doneEvents = new WaitHandle[_config.Connections.Count];

				var i = 0;
				foreach (var connection in _config.Connections)
				{
					var doneEvent = new ManualResetEvent(false);
					_downloadManagers.Add(new FtpDownloadManager
						{
							Connection = connection,
							ResetEvent = doneEvent
						});
					_doneEvents[i] = doneEvent;
					i++;					
				}

				foreach (var downloadManager in _downloadManagers)
				{
					downloadManager.Start();
				}

				// wait until all tasks are processed
				WaitHandle.WaitAll(_doneEvents);

				return true;
			}
			return false;
		}
	}
}