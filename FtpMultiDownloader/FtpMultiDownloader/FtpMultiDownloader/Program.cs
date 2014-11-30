using System;
using System.Collections.Generic;
using System.IO;
using FtpMultiDownloader.Conf;
using FtpMultiDownloader.Ftp;
using log4net;

namespace FtpMultiDownloader
{
	public class Program
	{
		private static readonly ILog Logger = LogManager.GetLogger(typeof(Program));

		public static void Main(string[] args)
		{
			var time = DateTime.Now;

			AppDomain.CurrentDomain.UnhandledException += AppDomainUnhandledException;

			// configure log4net
			log4net.Config.XmlConfigurator.ConfigureAndWatch(new FileInfo(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "ftpmultidownloader.logging")));

			Logger.Info("FtpMultiDownloader startet.");

			var configLoader = new ConfigLoader(Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "ftpmultidownloader.config"));

			var manager = new FtpMultiDownloadManager(configLoader.Config);

			Logger.Info("FtpMultiDownloader initialized.");

			var success = manager.Validate();

			if (!success)
			{
				Logger.Error("FtpMultiDownloader invalid configuration. Closing application.");
				return;
			}

			Logger.Info("FtpMultiDownloader configuration validated.");

			Logger.Info("FtpMultiDownloader starting timer.");

			success = manager.Start();

			if (!success)
			{
				Logger.Error("FtpMultiDownloader exception during file downloads. Closing application.");
			}
			var timespan = (DateTime.Now - time);
			Logger.InfoFormat("Download took: {0}:{1}:{2}", timespan.Hours, timespan.Minutes, timespan.Seconds);
		}

		

		#region Global exception handling

		private static void AppDomainUnhandledException(object sender, UnhandledExceptionEventArgs e)
		{
			var ex = e.ExceptionObject as Exception;
			Logger.Error("Unhandled exception occured during runtime.", ex);
			HandleException(ex);
		}

		private static void HandleException(Exception ex)
		{
			if (ex != null)
			{
				Logger.Error("Handled exception", ex);
			}
			Environment.Exit(1);
		}

		#endregion
	}
}
