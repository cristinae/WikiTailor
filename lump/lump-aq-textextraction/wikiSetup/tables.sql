-- phpMyAdmin SQL Dump
-- version 2.8.0.3-Debian-1
-- http://www.phpmyadmin.net
-- 
-- Host: localhost
-- Erstellungszeit: 05. Mai 2008 um 16:02
-- Server Version: 5.0.22
-- PHP-Version: 5.1.2
-- 
-- Datenbank: `wikiapi_ar`
-- 

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `Category`
-- 

CREATE TABLE IF NOT EXISTS `Category` (
  `id` bigint(20) NOT NULL auto_increment,
  `pageId` int(11) default NULL,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `pageId` (`pageId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=302302 ;

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `MetaData`
-- 

CREATE TABLE IF NOT EXISTS `MetaData` (
  `id` bigint(20) NOT NULL auto_increment,
  `language` varchar(255) default NULL,
  `disambiguationCategory` varchar(255) default NULL,
  `mainCategory` varchar(255) default NULL,
  `nrofPages` bigint(20) default NULL,
  `nrofRedirects` bigint(20) default NULL,
  `nrofDisambiguationPages` bigint(20) default NULL,
  `nrofCategories` bigint(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `Page`
-- 

CREATE TABLE IF NOT EXISTS `Page` (
  `id` bigint(20) NOT NULL auto_increment,
  `pageId` int(11) default NULL,
  `name` varchar(255) default NULL,
  `text` longtext,
  `isDisambiguation` bit(1) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `pageId` (`pageId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=302898 ;

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `PageMapLine`
-- 

CREATE TABLE IF NOT EXISTS `PageMapLine` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `pageID` int(11) default NULL,
  `stem` varchar(255) default NULL,
  `lemma` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=302898 ;

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `category_inlinks`
-- 

CREATE TABLE IF NOT EXISTS `category_inlinks` (
  `id` bigint(20) NOT NULL,
  `inLinks` int(11) default NULL,
  KEY `FK3F433773E46A97CC` (`id`),
  KEY `FK3F433773BB482769` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `category_outlinks`
-- 

CREATE TABLE IF NOT EXISTS `category_outlinks` (
  `id` bigint(20) NOT NULL,
  `outLinks` int(11) default NULL,
  KEY `FK9885334CE46A97CC` (`id`),
  KEY `FK9885334CBB482769` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `category_pages`
-- 

CREATE TABLE IF NOT EXISTS `category_pages` (
  `id` bigint(20) NOT NULL,
  `pages` int(11) default NULL,
  KEY `FK71E8D943E46A97CC` (`id`),
  KEY `FK71E8D943BB482769` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `page_categories`
-- 

CREATE TABLE IF NOT EXISTS `page_categories` (
  `id` bigint(20) NOT NULL,
  `pages` int(11) default NULL,
  KEY `FK72FB59CC1E350EDD` (`id`),
  KEY `FK72FB59CC75DCF4FA` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `page_inlinks`
-- 

CREATE TABLE IF NOT EXISTS `page_inlinks` (
  `id` bigint(20) NOT NULL,
  `inLinks` int(11) default NULL,
  KEY `FK91C2BC041E350EDD` (`id`),
  KEY `FK91C2BC0475DCF4FA` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `page_outlinks`
-- 

CREATE TABLE IF NOT EXISTS `page_outlinks` (
  `id` bigint(20) NOT NULL,
  `outLinks` int(11) default NULL,
  KEY `FK95F640DB1E350EDD` (`id`),
  KEY `FK95F640DB75DCF4FA` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `page_redirects`
-- 

CREATE TABLE IF NOT EXISTS `page_redirects` (
  `id` bigint(20) NOT NULL,
  `redirects` varchar(255) default NULL,
  KEY `FK1484BA671E350EDD` (`id`),
  KEY `FK1484BA6775DCF4FA` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


