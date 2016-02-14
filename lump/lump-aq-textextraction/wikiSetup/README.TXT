# careful: information here overlaps with that in lump/trunk/lump-wiki/log

1. Create a MySQL database (use UTF-8 encoding).
2. Use "mysql -uUSER -p DATABASE_NAME < tables.sql" to create the tables.
3. Download the Wikipedia Data from http://download.wikimedia.org/backup-index.html
 You need:
   * [LANGCODE]wiki-[DATE]-pages-articles.xml.bz2
   * [LANGCODE]wiki-[DATE]-pagelinks.sql.gz
   * [LANGCODE]wiki-[DATE]-categorylinks.sql.gz
4. Unzip everything
5. Run the transformation.
 java -jar JWPLDataMachine.jar [LANGUAGE] [MAIN_CATEGORY_NAME] [DISAMBIGUATION_CATEGORY_NAME] [SOURCE_DIRECTORY]
 * LANGUAGE - a language string matching one in languages.txt in this release.
 * MAIN_CATEGORY_NAME - the name of the main (top) category of the Wikipedia
                        category hierarchy
 * DISAMBIGUATION_CATEGORY_NAME - the name of the category that contains the
                                  disambiguation categories
 * SOURCE_DIRECTORY - the path to the directory containing the source files

 Examples:
 'java -jar JWPLDataMachine.jar english Categories Disambiguation ~/enwiki/20081013/'
 'java -jar JWPLDataMachine.jar russian Всё Многозначные термины ~/ruwiki/20080420/'
 'java -jar JWPLDataMachine.jar spanish Índice de categorías Desambiguación ~/eswiki/20080416/'
 'java -jar JWPLDataMachine.jar japanese 主要カテゴリ 曖昧さ回避 ~/jawiki/20080408/'
 'java -jar JWPLDataMachine.jar german !Hauptkategorie Begriffsklärung ~/dewiki/20080422/'
 'java -jar JWPLDataMachine.jar dutch Alles Doorverwijspagina ~/nlwiki/200810408/'

6. This should create a lot of new data files in a "output" subfolder of each
   input folder.
7. Import these data files into the database.
 * If there are only the output files in that directory you can use:
   * 'mysqlimport -uUSER -p --default-character-set=utf8 {database_name} `pwd`/*.txt'
 * Otherwise, you need to do it the long way:
    * 'mysqlimport -uUSER -p --default-character-set=utf8 {database_name} {txt_file1} {txt_file2} ... {txt_file_n}'

 * If you encounter a “broken pipe” error in this step, try adding the
   --max_allowed_packet parameter to the above query. Set it to something
   reasonable high, e.g., --max_allowed_packet=128M.
 * Setting the --max_allowed_packet parameter on the console only changes
   it for the client, but the problem can also be on the server side.
   Thus, if adding max_allowed_packet=128M doesn’t work on the command line,
   try entering it into the my.cnf file in the MySql directory under the
   [mysqld] section. 
8. When first connecting to a newly imported database, indexes are created.
   This takes some time (up to 30 minutes), depending on the server and the
   size of your Wikipedia. Subsequent connects won't have this delay.
