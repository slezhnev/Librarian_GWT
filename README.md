Librarian_GWT
=============

Web library manager for lib.rus.ec

Converter - data converter from Librarian to Librarian.GWT
Downloader - standalone downloader for downloading marked "for reading" books from Librarian.GWT to reader (PC or e-ink or something else)
Librarian.GWT - main module

Compiled project - at "Librarian.GWT/war"

If you want to use Tomcat as servlet container - you must add "URIEncoding="UTF-8"" parameter to the http connector in server.xml configuration file.
Example:
    <Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443"
               URIEncoding="UTF-8" />
