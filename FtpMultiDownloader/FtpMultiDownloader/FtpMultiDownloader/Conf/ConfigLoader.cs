using System;
using System.Collections.Generic;
using System.IO;
using System.Xml.Serialization;
using log4net;

namespace FtpMultiDownloader.Conf
{
    /// <summary>
    /// Configuration of the offline client
    /// </summary>
    internal class ConfigLoader
    {
        private static readonly ILog Logger = LogManager.GetLogger(typeof(ConfigLoader));

        private readonly string _filename;
        public FtpMultiDownloader Config { get; set; }

        public ConfigLoader(string filename)
        {
            _filename = filename;
            LoadFromXml();
        }

        /// <summary>
        /// Deserialize Config-File to an object
        /// </summary>
        private void LoadFromXml()
        {
            if (!File.Exists(_filename))
            {
                File.Create(_filename);
            }
            try
            {
                using (var fs = new FileStream(_filename, FileMode.Open, FileAccess.Read))
                {
                    var serializer = new XmlSerializer(typeof (FtpMultiDownloader));
                    Config = (FtpMultiDownloader) serializer.Deserialize(fs);
                }
            }
            catch (Exception)
            {
                Config = new FtpMultiDownloader();
                Save();
            }
        }

        /// <summary>
        /// Serialize Config-File to an object
        /// </summary>
        public void Save()
        {
            try
            {
				File.Delete(_filename);
                using (var fs = new FileStream(_filename, FileMode.Create, FileAccess.ReadWrite))
                {
                    var serializer = new XmlSerializer(typeof (FtpMultiDownloader));
                    try
                    {
                        serializer.Serialize(fs, Config);
                    }
                    catch (Exception e)
                    {
                        Logger.Error("Config serialize error", e);
                    }
                }
            }
            catch (Exception e)
            {
                Logger.Error("Config file save error", e);
            }
        }
    }

    /// <summary>
    /// XML-Definition
    /// </summary>
	[XmlRoot("FtpMultiDownloader")]
    public class FtpMultiDownloader
    {
	    public List<ConnectionInformation> Connections;
		public int Threads { get; set; }
    }

	public class ConnectionInformation
	{
		public string Url { get; set; }
		public string Port { get; set; }
		public string Username { get; set; }
		public string Password { get; set; }
		public string Filepath { get; set; }
		public string DestinationFolder { get; set; }
	}
}