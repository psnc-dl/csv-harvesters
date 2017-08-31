archiveOutput.csv contains all data about parsed archives.

You can set mode that the program will be working in
by writing 1, 2 or 3 in setMode.txt:

1 means reading new web pages with digital data to file addressesAlreadyRead.txt
2 means parsing data from webPage from addressesAlreadyRead.txt 
3 meand reading new pages and parsing

All archives url that couldn't be parsed are added to timeOutPages.txt
All archives that addresses has been read but not parsed are in addressWithDigitalData.txt 