# Lucyna

Java application for searching text in documents of various formats
(TXT, PDF, RTF, ODF, OOXML) and in different languages of user's choice:
English and Polish.
The project consists of two main subprograms: Indexer and Searcher.

## Technology stack
* Java 11
* Apache Tika (for text extraction)
* Apache Lucene (for full-text search)

## Features

* Automatic document indexing (on the fly, when app running)
* Text extraction (different formats)
* Text searching in documents:
    * term query (single keyword search)
    * phrase query
    * fuzzy query
* CLI Tool (REPL Interpreter) for convenient searching

## Examples of CLI usage

### Searcher:
```console
> %lang pl
> %details on
> %color on
> %term
> blabaliza
Files count: 1
/home/testuser/Documents/rblarba-pl.pdf:
Wykorzystanie teorii Fifaka daje wreszcie możliwość
efektywnego wykonania blabalizy numerycznej. ... Podejście wprost
Najprostszym sposobem wykonania blabalizy jest siłowe przeszukanie całej przestrzeni rozwiązań. ... W literaturze można znaleźć kilka prób opracowania heurystyk dla problemu blabalizy
(por. ... Korzystając z heurystyk daje się z pewnym trudem dokonać blabalizy w przestrzeni o np. 500 fetorach bazowych. ... Zasadnicza różnica w stosunku do innych metod blabalizy polega na tym, że przedstawienie
> %fuzzy
> rower
Files count: 1
/home/testuser/Documents/rblarba-pl.pdf:
Uniwersytet Warszawski
Wydział Matematyki, Informatyki i Mechaniki
Robert Blarbarucki
Nr albumu: 1337
Implementacja blabalizatora
różnicowego z wykorzystaniem
teorii fetorów ?-?
Praca magisterska
na kierunku INFORMATYKA
Praca wykonana pod kierunkiem
dr. hab. ... tłum. z chińskiego Robert Blarbarucki
>
```

### Indexer arguments:

You can run the indexer with (at most one)
of the following arguments this way:

```java -jar indexer.jar --argName```

| Argument | Meaning |
| --- | ----------- |
| ```--purge``` | Remove all documents from index. |
| ```--add <dir>``` | Add the `<dir>` directory to the set of indexed (and watched) directories and recursively index all files under this directory.|
| ```--rm <dir>``` | Remove directory ```<dir>``` from the set of indexed catalogues and remove all files in it, also in subfolders, recursively. |
| ```--reindex``` | Remove all indexed files and index them once again, using --add option. |
| ```--list``` | Print list of all directories included in the index (added with --add option). |

## Installation

To build the app just use Maven `mvn package`
It will build two jars `searcher.jar` and `indexer.jar` which are the corresponding
parts of the application.



