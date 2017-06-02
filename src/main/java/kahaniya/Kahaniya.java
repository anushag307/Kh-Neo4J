package kahaniya;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.backup.OnlineBackup;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Lock;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.lucene.QueryContext;

import kahaniya.KahaniyaCustomException;

public class Kahaniya implements KahaniyaService.Iface{

	//Index names
	public static final String GENRE_NAME_INDEX = "genre_index_by_name";
	public static final String LANG_NAME_INDEX = "language_index_by_name";
	public static final String GENRE_LANG_INDEX = "genre_language_index_by_name";
	public static final String USER_ID_INDEX = "user_index_by_id";
	public static final String USER_NAME_INDEX = "user_index_by_uname";
	public static final String USER_EMAIL_INDEX = "user_index_by_email";
	public static final String COMMENT_ID_INDEX = "comment_index_by_id";
	public static final String WRITING_STYLE_NAME_INDEX = "writing_style_index_by_name";

	public static final String SERIES_ID_INDEX = "series_index_by_id";
	public static final String REVIEW_ID_INDEX = "review_index_by_id";
	public static final String CONTEST_ID_INDEX = "contest_index_by_id";
	public static final String CONTEST_TITLE_ID_INDEX = "contest_index_by_title_id";
	public static final String CHAPTER_ID_INDEX = "chapter_index_by_id";
	public static final String CHAPTER_TITLE_ID_INDEX = "chapter_index_by_title_id";
	public static final String CLOSED_CHAPTER_ID_INDEX = "closed_chapter_index_by_id";
	public static final String CLOSED_SERIES_ID_INDEX = "closed_chapter_index_by_id";
	
	public static final String SERIES_TITLE_ID_INDEX = "series_index_by_title_id";
	public static final String SERIES_TYPE_INDEX = "series_index_by_type";
	
	
	public static final String ANTHOLOGY_ID_INDEX = "anthology_index_by_id";
	public static final String ANTHOLOGY_TITLE_ID_INDEX = "anthology_index_by_title_id";
	
	public static final String NANOSTORY_ID_INDEX = "nanostory_index_by_id";
	
	public static final String PURCHASED_CHAPTER_ID_INDEX = "purchased_chapter_index_by_id";
	public static final String PURCHASED_SERIES_ID_INDEX = "purchased_series_index_by_id";
	
	

	public static final String USER_VIEWED_CHAPTER_REL_INDEX = "user_viewed_chapter_relation_index";
	public static final String USER_READ_CHAPTER_REL_INDEX = "user_read_chapter_relation_index";
	
	public static final String USER_PURCHASED_CHAPTER_REL_INDEX = "user_purchased_chapter_relation_index";
	public static final String USER_PURCHASED_SERIES_REL_INDEX = "user_purchased_series_relation_index";
	public static final String USER_PURCHASED_ANTHOLOGY_REL_INDEX = "user_purchased_anthology_relation_index";
	

	public static final String KEYWORD_INDEX = "keyword_index_by_name";
	
	public static final String SEARCH_INDEX = "search_index_new";
	
	public static final String LOCK_INDEX = "lock_index";
	
	//search related keys
	public static final String SEARCH_USER = "search_user";
	public static final String SEARCH_CHAPTER = "search_chapter";
	public static final String SEARCH_SERIES = "search_series";
	public static final String SEARCH_ANTHOLOGY = "search_anthology";
	public static final String SEARCH_NANOSTORY = "search_nanostory";
	public static final String SEARCH_CONTEST = "search_contest";

	//lock related keys
	public static final String LockName = "lock_node";
	
	//series status index related keys
	public static final String SERIES_STATUS = "series_status";
	
	//writing style node related keys
	public static final String WRITING_STYLE_NAME = "writing_style_name";

	//review related keys
	public static final String REVIEW_ID = "review_id";
	public static final String REVIEW_DATA = "review_data";
	
	//nanostory related keys
	public static final String NANOSTORY_ID = "nanostory_id";
	public static final String NANOSTORY_FEAT_IMG = "nanostory_feat_img";
	public static final String NANOSTORY_VIEWS = "nanostory_total_view_count";
	public static final String NANOSTORY_CONTENT = "nanostory_content";
	
	
	//chapter related keys
	public static final String CHAPTER_ID = "chapter_id";
	public static final String CHAPTER_TITLE = "chapter_title";
	public static final String CHAPTER_TITLE_ID = "chapter_title_id";
	public static final String CHAPTER_FEAT_IMAGE = "chapter_feat_image";
	public static final String CHAPTER_FREE_OR_PAID = "chapter_free_or_paid";
	public static final String CHAPTER_WORDS_COUNT = "chapter_words_count";
	public static final String TOTAL_CHAPTER_VIEWS = "chapter_total_view_count";
	public static final String TOTAL_CHAPTER_READS = "chapter_total_read_count";
	public static final String CHAPTER_LAST_VIEWED_TIME = "chapter_last_viewed_time";
	
	//chapter - user relationship properties
	public static final String CHAPTER_RATING = "chapter_rating"; // user has given a rating for a chapter
	public static final String CHAPTER_VIEWS = "chapter_views"; // number of times a logged in user has vied a chapter
	public static final String CHAPTER_READ_WORD_COUNT = "chapter_read_word_count"; // number of words, a logged in user has read in a chapter
	public static final String CHAPTER_READ_STATUS = "chapter_read_status"; // chapter read status of a logged in user
	public static final String CHAPTER_PURCHASED_PRICE = "chapter_purchased_price"; // price that a user has paid to purchase a chapter 
	public static final String CHAPTER_CONTEST_STATUS = "chapter_contest_status"; // status of the chapter for the given contest 
	public static final String CHAPTER_CONTEST_RATED_BY_JUDGES = "chapter_contest_rated_by_judges"; // status of the chapter for the given contest 

	
	public static final String SERIES_CONTEST_STATUS = "series_contest_status"; // status of the series for the given contest
	public static final String SERIES_CONTEST_RATED_BY_JUDGES = "series_contest_rated_by_judges"; // status of the series for the given contest
	public static final String NANOSTORY_CONTEST_STATUS = "nanostory_contest_status"; // status of the nanostory for the given contest
	public static final String NANOSTORY_CONTEST_RATED_BY_JUDGES = "nanostory_contest_rated_by_judges"; // status of the nanostory for the given contest
	
	//user viewed a chapter related key // Note: do not correct this spelling mistake, as the mistake was observed after staging
	public static final String USER_VIEWED_A_CHAPTER_ID = "user_viewer_a_chapter_id"; 
	//user read a chapter related key
	public static final String USER_READ_A_CHAPTER_ID = "user_read_a_chapter_id";
		
	//user related keys
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String NTFS_LAST_SEEN = "ntfs_last_seen";
	public static final String FULL_NAME = "full_name";
	public static final String EMAIL = "email";
	public static final String MOBILE_NUMBER = "mobile_number";
	public static final String MOBILE_DIAL_CODE = "mobile_dial_code";
	public static final String DOB = "dob";
	public static final String GENDER = "gender";
	public static final String ADDRESS = "address";
	public static final String BIO = "bio";
	public static final String PRIVILEGE = "privilege";
	public static final String STATUS = "status";
	public static final String TIME_CREATED = "time_created";
	public static final String TIME_EDITED = "time_edited";
	public static final String IS_DELETED = "is_deleted";
	
	//genre node related keys
	public static final String GENRE_NAME = "genre_name";

	//language node related keys
	public static final String LANG_NAME = "language_name";
	
	//genre_language node related keys
	public static final String GENRE_LANG_NAME = "genre_language_name";
	
	//keyword node related keys
	public static final String KEYWORD_NAME = "keyword_name";
	
	//Anthology related keys
	public static final String ANTHOLOGY_ID = "anthology_id";
	public static final String ANTHOLOGY_TITLE = "anthology_title";
	public static final String ANTHOLOGY_TITLE_ID = "anthology_title_id";
	public static final String ANTHOLOGY_SUMMARY = "anthology_summary";
	public static final String ANTHOLOGY_FEAT_IMG = "anthology_feat_img";
	
	public static final String ANTHOLOGY_PURCHASED_PRICE = "anthology_purchased_price"; // price that a user has paid to purchase a anthology
		
	//series node related keys
	public static final String SERIES_ID = "series_id";
	public static final String SERIES_TITLE = "series_title";
	public static final String SERIES_TITLE_ID = "series_title_id";
	public static final String SERIES_TAG_LINE = "series_tag_line";
	public static final String SERIES_FEAT_IMG = "series_feat_img";
	public static final String SERIES_KEYWORDS = "series_keywords";
	public static final String SERIES_COPYRIGHTS = "series_copyrights";
	public static final String SERIES_DD_IMG = "series_dd_img";
	public static final String SERIES_DD_SUMMARY = "series_dd_summary";
	public static final String SERIES_TYPE = "series_type";
	public static final String SERIES_PRICE = "series_price";
	
	public static final String SERIES_PURCHASED_PRICE = "series_purchased_price"; // price that a user has paid to purchase a series
	
	//contest node related keys
	public static final String CONTEST_ID = "contest_id";
	public static final String CONTEST_TITLE = "contest_title";
	public static final String CONTEST_TITLE_ID = "contest_title_id";
	public static final String CONTEST_SUMMARY = "contest_summary";
	public static final String CONTEST_DESCRIPTION = "contest_description";
	public static final String CONTEST_FEAT_IMAGE = "contest_feat_image";
	public static final String CONTEST_AWARD_INFO = "contest_award_info";
	public static final String CONTEST_THEME_INFO= "contest_theme_info";
	public static final String CONTEST_PARTNERS_INFO = "contest_partners_info";
	public static final String CONTEST_START_DATE = "contest_start_date";
	public static final String CONTEST_END_DATE = "contest_end_date";
	public static final String CONTEST_LANGUAGE = "contest_language";
	public static final String CONTEST_PRICE = "contest_price";
	public static final String CONTEST_TYPE = "contest_type";
	public static final String CONTEST_MODE = "contest_mode";
	public static final String CONTEST_VISIBILTY = "contest_visibilty";
		

	
	//comment related keys
	public static final String COMMENT_ID = "comment_id";
	public static final String COMMENT_CONTENT = "comment_content";
	
	public static final String NODE_TYPE = "node_type";
	public static final String LOCK_NODE = "lock_node";
	public static final String SERIES_STATUS_BASE_NODE = "series_status_base_node"; //search on key
	public static final String SERIES_STARTED_BASE_NODE = "series_started_base_node"; // search value to get started node
	public static final String SERIES_ONGOING_BASE_NODE = "series_ongoing_base_node"; // search value to get completed node
	public static final String SERIES_COMPLETED_BASE_NODE = "series_completed_base_node"; // search value to get completed node
	public static final String USER_NODE = "user_node";
	public static final String GENRE_NODE = "genre_node";
	public static final String LANG_NODE = "language_node";
	public static final String GENRE_LANG_NODE = "genre_language_node";
	public static final String SERIES_NODE = "series_node";
	public static final String KEYWORD_NODE = "keyword_node";
	public static final String REVIEW_NODE = "review_node";
	public static final String CHAPTER_NODE = "chapter_node";
	public static final String ANTHOLOGY_NODE = "anthology_node";
	public static final String NANOSTORY_NODE = "nanostory_node";
	public static final String COMMENT_NODE = "comment_node";
	public static final String CONTEST_NODE = "contest_node";
	public static final String WRITING_STYLE_NODE = "writing_style_node";
	
	private static GraphDatabaseService graphDb = null;
		
	//Relationship names
	public static final RelationshipType USER_INTERESTED_GENRE = DynamicRelationshipType.withName("USER_INTERESTED_GENRE");
	public static final RelationshipType USER_INTERESTED_LANGUAGE = DynamicRelationshipType.withName("USER_INTERESTED_LANGUAGE");
	public static final RelationshipType USER_FOLLOW_USER = DynamicRelationshipType.withName("USER_FOLLOW_USER");
	public static final RelationshipType USER_STARTED_SERIES = DynamicRelationshipType.withName("USER_STARTED_SERIES");
	public static final RelationshipType USER_STARTED_SERIES_ON_CLOSED_CONTEST = DynamicRelationshipType.withName("USER_STARTED_SERIES_ON_CLOSED_CONTEST");
	public static final RelationshipType USER_STARTED_ANTHOLOGY = DynamicRelationshipType.withName("USER_STARTED_ANTHOLOGY");
		
	
	public static final RelationshipType USER_SKIP_FOLLOW_USER = DynamicRelationshipType.withName("USER_SKIP_FOLLOW_USER");

	public static final RelationshipType SERIES_BELONGS_TO_GENRE = DynamicRelationshipType.withName("SERIES_BELONGS_TO_GENRE");
	public static final RelationshipType SERIES_BELONGS_TO_LANGUAGE = DynamicRelationshipType.withName("SERIES_BELONGS_TO_LANGUAGE");
	public static final RelationshipType SERIES_BELONGS_TO_GENRE_LANGUAGE = DynamicRelationshipType.withName("SERIES_BELONGS_TO_GENRE_LANGUAGE");
	public static final RelationshipType SERIES_STARTED_REL_TO_BASE_NODE = DynamicRelationshipType.withName("SERIES_STARTED_REL_TO_BASE_NODE");
	public static final RelationshipType SERIES_ONGOING_REL_TO_BASE_NODE = DynamicRelationshipType.withName("SERIES_ONGOING_REL_TO_BASE_NODE");
	public static final RelationshipType SERIES_COMPLETED_REL_TO_BASE_NODE = DynamicRelationshipType.withName("SERIES_COMPLETED_REL_TO_BASE_NODE");
	public static final RelationshipType SERIES_KEYWORD = DynamicRelationshipType.withName("SERIES_KEYWORD");
	
	public static final RelationshipType ANTHOLOGY_BELONGS_TO_GENRE = DynamicRelationshipType.withName("ANTHOLOGY_BELONGS_TO_GENRE");
	public static final RelationshipType ANTHOLOGY_BELONGS_TO_LANGUAGE = DynamicRelationshipType.withName("ANTHOLOGY_BELONGS_TO_LANGUAGE");
	public static final RelationshipType ANTHOLOGY_BELONGS_TO_GENRE_LANGUAGE = DynamicRelationshipType.withName("ANTHOLOGY_BELONGS_TO_GENRE_LANGUAGE");
	
	public static final RelationshipType ANTHOLOGY_TAGGED_A_POST = DynamicRelationshipType.withName("ANTHOLOGY_TAGGED_A_POST");
	
	
	public static final RelationshipType NANOSTORY_BELONGS_TO_GENRE = DynamicRelationshipType.withName("NANOSTORY_BELONGS_TO_GENRE");
	public static final RelationshipType NANOSTORY_BELONGS_TO_LANGUAGE = DynamicRelationshipType.withName("NANOSTORY_BELONGS_TO_LANGUAGE");
	public static final RelationshipType NANOSTORY_BELONGS_TO_GENRE_LANGUAGE = DynamicRelationshipType.withName("NANOSTORY_BELONGS_TO_GENRE_LANGUAGE");
	

	//Relationships for Pinned
	public static final RelationshipType USER_PINNED_TO_LANGUAGE = DynamicRelationshipType.withName("USER_PINNED_TO_LANGUAGE");
	public static final RelationshipType CHAPTER_PINNED_TO_LANGUAGE = DynamicRelationshipType.withName("CHAPTER_PINNED_TO_LANGUAGE");
	public static final RelationshipType SERIES_PINNED_TO_LANGUAGE = DynamicRelationshipType.withName("SERIES_PINNED_TO_LANGUAGE");
	public static final RelationshipType ANTHOLOGY_PINNED_TO_LANGUAGE = DynamicRelationshipType.withName("ANTHOLOGY_PINNED_TO_LANGUAGE");
	
	public static final RelationshipType USER_WRITTEN_A_REVIEW = DynamicRelationshipType.withName("USER_WRITTEN_A_REVIEW");
	public static final RelationshipType REVIEW_BELONGS_TO_SERIES = DynamicRelationshipType.withName("REVIEW_BELONGS_TO_SERIES");
	public static final RelationshipType USER_SUBSCRIBED_TO_SERIES = DynamicRelationshipType.withName("USER_SUBSCRIBED_TO_SERIES");	
	public static final RelationshipType USER_FAV_CHAPTER = DynamicRelationshipType.withName("USER_FAV_CHAPTER");
	public static final RelationshipType USER_BOOKMARK_CHAPTER = DynamicRelationshipType.withName("USER_BOOKMARK_CHAPTER");
	public static final RelationshipType USER_RATED_A_CHAPTER = DynamicRelationshipType.withName("USER_RATED_A_CHAPTER");
	public static final RelationshipType USER_VIEWED_A_CHAPTER = DynamicRelationshipType.withName("USER_VIEWED_A_CHAPTER");	
	public static final RelationshipType USER_READ_A_CHAPTER = DynamicRelationshipType.withName("USER_READ_A_CHAPTER");	
	public static final RelationshipType USER_PURCHASED_A_CHAPTER = DynamicRelationshipType.withName("USER_PURCHASED_A_CHAPTER");
	public static final RelationshipType USER_PURCHASED_A_SERIES = DynamicRelationshipType.withName("USER_PURCHASED_A_SERIES");
	public static final RelationshipType USER_PURCHASED_A_ANTHOLOGY = DynamicRelationshipType.withName("USER_PURCHASED_A_ANTHOLOGY");
	public static final RelationshipType USER_WRITTEN_A_CHAPTER = DynamicRelationshipType.withName("USER_WRITTEN_A_CHAPTER");
	public static final RelationshipType USER_WRITTEN_A_CHAPTER_TO_CLOSED_CONTEST = DynamicRelationshipType.withName("USER_WRITTEN_A_CHAPTER_TO_CLOSED_CONTEST");
	public static final RelationshipType CHAPTER_BELONGS_TO_SERIES = DynamicRelationshipType.withName("CHAPTER_BELONGS_TO_SERIES");	
	public static final RelationshipType CHAPTER_BELONGS_TO_WRITING_STYLE = DynamicRelationshipType.withName("CHAPTER_BELONGS_TO_WRITING_STYLE");	
	public static final RelationshipType SERIES_BELONGS_TO_WRITING_STYLE = DynamicRelationshipType.withName("SERIES_BELONGS_TO_WRITING_STYLE");	
	public static final RelationshipType CONTEST_BELONGS_TO_WRITING_STYLE = DynamicRelationshipType.withName("CONTEST_BELONGS_TO_WRITING_STYLE");	
	public static final RelationshipType CHAPTER_BELONGS_TO_CONTEST = DynamicRelationshipType.withName("CHAPTER_BELONGS_TO_CONTEST");	
	public static final RelationshipType SERIES_BELONGS_TO_CONTEST = DynamicRelationshipType.withName("SERIES_BELONGS_TO_CONTEST");
	public static final RelationshipType NANOSTORY_BELONGS_TO_CONTEST = DynamicRelationshipType.withName("NANOSTORY_BELONGS_TO_CONTEST");
	public static final RelationshipType USER_STARTED_CONTEST = DynamicRelationshipType.withName("USER_STARTED_CONTEST");	
	public static final RelationshipType USER_SUBSCRIBED_TO_CONTEST = DynamicRelationshipType.withName("USER_SUBSCRIBED_TO_CONTEST");
	public static final RelationshipType USER_WRITTEN_A_NANOSTORY = DynamicRelationshipType.withName("USER_WRITTEN_A_NANOSTORY");
	public static final RelationshipType USER_LIKED_NANOSTORY = DynamicRelationshipType.withName("USER_LIKED_NANOSTORY");
	public static final RelationshipType USER_DISLIKED_NANOSTORY = DynamicRelationshipType.withName("USER_DISLIKED_NANOSTORY");
	public static final RelationshipType USER_VIEWED_A_NANOSTORY = DynamicRelationshipType.withName("USER_VIEWED_A_NANOSTORY");
	
	
	public static final RelationshipType USER_SKIP_SUBSCRIBED_TO_SERIES = DynamicRelationshipType.withName("USER_SKIP_SUBSCRIBED_TO_SERIES");
	
	public static final RelationshipType USER_WRITTEN_A_COMMENT = DynamicRelationshipType.withName("USER_WRITTEN_A_COMMENT");
	public static final RelationshipType COMMENT_WRITTEN_ON_CHAPTER = DynamicRelationshipType.withName("COMMENT_WRITTEN_ON_CHAPTER");
	public static final RelationshipType COMMENT_WRITTEN_ON_CONTEST = DynamicRelationshipType.withName("COMMENT_WRITTEN_ON_CONTEST");
	public static final RelationshipType REPLY_COMMENT_WRITTEN_ON_COMMENT = DynamicRelationshipType.withName("REPLY_COMMENT_WRITTEN_ON_COMMENT");	
	
	private static Comparator<Node> TimeCreatedComparatorForNodes = new Comparator<Node>() {
		public int compare(Node n1, Node n2) {
		   int v1 = 0;
		   int v2 = 0;
		   if(n1.hasProperty(TIME_CREATED))
			   v1 = Integer.parseInt(n1.getProperty(TIME_CREATED).toString());
		   if(n2.hasProperty(TIME_CREATED))
			   v2 = Integer.parseInt(n2.getProperty(TIME_CREATED).toString());
		   //ascending order
		   //return v1-v2;
		   //descending order
		   return v2-v1;
	    }};
	    
	private static Comparator<Node> EndTimeComparatorForContestNodes = new Comparator<Node>() {
		public int compare(Node n1, Node n2) {
		   int v1 = 0;
		   int v2 = 0;
		   if(n1.hasProperty(CONTEST_END_DATE))
			   v1 = Integer.parseInt(n1.getProperty(CONTEST_END_DATE).toString());
		   if(n2.hasProperty(CONTEST_END_DATE))
			   v2 = Integer.parseInt(n2.getProperty(CONTEST_END_DATE).toString());
		   //ascending order
		   //return v1-v2;
		   //descending order
		   return v2-v1;
	    }};
	    
	private static Comparator<Node> TrendingComparatorForChapterNodes = new Comparator<Node>() {
		public int compare(Node n1, Node n2) {
		   int v1 = 0;
		   int v2 = 0;
		   int t = (int)(new Date().getTime()/1000) - (1*24*60*60); // made it to last 24 hours
		   
		   Iterator<Relationship> loggedInViews = n1.getRelationships(USER_VIEWED_A_CHAPTER).iterator();   
		   while(loggedInViews.hasNext())
		   {
				Relationship rel = loggedInViews.next();
				if(Integer.parseInt(rel.getProperty(TIME_CREATED).toString()) >= t)
					v1++;
		   }
		   Iterator<Relationship> ratings = n1.getRelationships(USER_RATED_A_CHAPTER).iterator();			   
		   while(ratings.hasNext())
		   {
				Relationship rel = ratings.next();
				if(Integer.parseInt(rel.getProperty(TIME_CREATED).toString()) >= t)
					v1 = v1+Integer.parseInt(rel.getProperty(CHAPTER_RATING).toString());
		   }
		   
		   loggedInViews = n2.getRelationships(USER_VIEWED_A_CHAPTER).iterator();   
		   while(loggedInViews.hasNext())
		   {
				Relationship rel = loggedInViews.next();
				if(Integer.parseInt(rel.getProperty(TIME_CREATED).toString()) >= t)
					v2 = v2+1;
		   }
		   ratings = n2.getRelationships(USER_RATED_A_CHAPTER).iterator();			   
		   while(ratings.hasNext())
		   {
				Relationship rel = ratings.next();
				if(Integer.parseInt(rel.getProperty(TIME_CREATED).toString()) >= t)
					v2 = v2+Integer.parseInt(rel.getProperty(CHAPTER_RATING).toString());
		   }			   
		   //ascending order
		   //return v1-v2;
		   //descending order
		   return v2-v1;
	    }};
		
	private static Comparator<Node> DiscoverComparatorForUserNodes = new Comparator<Node>() {
		public int compare(Node n1, Node n2) {
		   int v1 = 0;
		   int v2 = 0;
		   
		   Iterator<Relationship> chapters1 = n1.getRelationships(USER_WRITTEN_A_CHAPTER).iterator();
		   while(chapters1.hasNext())
		   {
			   v1 = v1 + Integer.parseInt(chapters1.next().getEndNode().getProperty(TOTAL_CHAPTER_VIEWS).toString());
		   }
		   
		   Iterator<Relationship> chapters2 = n2.getRelationships(USER_WRITTEN_A_CHAPTER).iterator();
		   while(chapters2.hasNext())
		   {
			   v2 = v2 + Integer.parseInt(chapters2.next().getEndNode().getProperty(TOTAL_CHAPTER_VIEWS).toString());
		   }
		   
		   //ascending order
		   //return v1-v2;
		   //descending order
		   return v2-v1;
	    }};
				
	private static Comparator<Node> TrendingComparatorForSeriesNodes = new Comparator<Node>() {
		public int compare(Node n1, Node n2) {
		   int v1 = 0;
		   int v2 = 0;
//		   int t = (int)(new Date().getTime()/1000) - (7*24*60*60);
		   Iterator<Relationship> chaptersItr1 = n1.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
		   while(chaptersItr1.hasNext())
		   {
			   v1 = v1 + Integer.parseInt(chaptersItr1.next().getStartNode().getProperty(TOTAL_CHAPTER_VIEWS).toString());
		   }
		   v1 = v1 + (10*n1.getDegree(USER_SUBSCRIBED_TO_SERIES));
		   Iterator<Relationship> chaptersItr2 = n2.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
		   while(chaptersItr2.hasNext()) 
		   {
			   v2 = v2 + Integer.parseInt(chaptersItr2.next().getStartNode().getProperty(TOTAL_CHAPTER_VIEWS).toString());
		   }
		   v2 = v2 + (10*n2.getDegree(USER_SUBSCRIBED_TO_SERIES));
		   //ascending order
		   //return v1-v2;
		   //descending order
		   return v2-v1;
	    }};
			
	private static Comparator<Relationship> TimeCreatedComparatorForRelationships = new Comparator<Relationship>() {
			public int compare(Relationship n1, Relationship n2) {
			   int v1 = 0;
			   int v2 = 0;
			   if(n1.hasProperty(TIME_CREATED))
				   v1 = Integer.parseInt(n1.getProperty(TIME_CREATED).toString());
			   if(n2.hasProperty(TIME_CREATED))
				   v2 = Integer.parseInt(n2.getProperty(TIME_CREATED).toString());
			   //ascending order
			   //return v1-v2;
			   //descending order
			   return v2-v1;
		    }
		}; 

	private static Comparator<Node> LastChapterAddedTimeComparatorForSeries = new Comparator<Node>() {
		public int compare(Node n1, Node n2) {
		   int v1 = 0;
		   int v2 = 0;
		   Iterator<Relationship> seriesChaptersRel1 = n1.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
		   while(seriesChaptersRel1.hasNext())
		   {
			   Relationship rel = seriesChaptersRel1.next();
			   if(Integer.parseInt(rel.getProperty(TIME_CREATED).toString()) > v1)
						   v1 = Integer.parseInt(rel.getProperty(TIME_CREATED).toString());
		   }
		   Iterator<Relationship> seriesChaptersRel2 = n2.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
		   while(seriesChaptersRel2.hasNext())
		   {
			   Relationship rel = seriesChaptersRel2.next();
			   if(Integer.parseInt(rel.getProperty(TIME_CREATED).toString()) > v2)
						   v2 = Integer.parseInt(rel.getProperty(TIME_CREATED).toString());
		   }
		   //ascending order
		   //return v1-v2;
		   //descending order
		   return v2-v1;
	    }};

	private static Comparator<Node> TrendingComparatorForAuthorNodes = new Comparator<Node>() {
		public int compare(Node n1, Node n2) {
		   int v1 = 0;
		   int v2 = 0;
		   int t = (int)(new Date().getTime()/1000) - (7*24*60*60);
		   Iterator<Relationship> chaptersItr1 = n1.getRelationships(USER_WRITTEN_A_CHAPTER).iterator();
		   while(chaptersItr1.hasNext())
		   {
			   Relationship rel = chaptersItr1.next();
			   if(Integer.parseInt(rel.getProperty(TIME_CREATED).toString()) >= t)
						   v1++;
		   }
		   Iterator<Relationship> chaptersItr2 = n2.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
		   while(chaptersItr2.hasNext()) 
		   {
			   Relationship rel = chaptersItr2.next();
			   if(Integer.parseInt(rel.getProperty(TIME_CREATED).toString()) >= t)
						   v2++;

		   }
		   //ascending order
		   //return v1-v2;
		   //descending order
		   return v2-v1;
	    }};
				

    private Node User(String id, String full_name, String user_name, String email, String mobile, String dial_code, String dob, String gender, String address, String bio, int privilege, int status, int time_created){
		Node node = graphDb.createNode();
		node.setProperty(USER_ID,id);
	    node.setProperty(FULL_NAME,full_name);
		node.setProperty(USER_NAME,user_name);
		node.setProperty(EMAIL,email);
		node.setProperty(MOBILE_NUMBER,mobile);
		node.setProperty(MOBILE_DIAL_CODE, dial_code);
		node.setProperty(DOB,dob);
		node.setProperty(GENDER,gender);
		node.setProperty(ADDRESS,address);
		node.setProperty(BIO,bio);
		node.setProperty(PRIVILEGE,privilege);
		node.setProperty(STATUS,status);
	    node.setProperty(TIME_CREATED,time_created);
	    node.setProperty(NODE_TYPE, USER_NODE);
	    return node;
	}

    private Node Comment(String id, String content, int time_created){
		Node node = graphDb.createNode();
		node.setProperty(COMMENT_ID,id);
	    node.setProperty(COMMENT_CONTENT,content);
		node.setProperty(TIME_CREATED,time_created);
	    node.setProperty(NODE_TYPE, COMMENT_NODE);
	    return node;
	} 

    private Node Genre(String name){
		Node node = graphDb.createNode();
		node.setProperty(GENRE_NAME,name);
	    node.setProperty(NODE_TYPE, GENRE_NODE);
	    return node;
	} 

    private Node GenreLang(String genre_name, String lang_name){
		Node node = graphDb.createNode();
		node.setProperty(GENRE_NAME,genre_name);
		node.setProperty(LANG_NAME,lang_name);
	    node.setProperty(NODE_TYPE, GENRE_LANG_NODE);
	    return node;
	} 

    private Node Keyword(String name){
		Node node = graphDb.createNode();
		node.setProperty(KEYWORD_NAME,name);
	    node.setProperty(NODE_TYPE, KEYWORD_NODE);
	    return node;
	} 
    
    private Node Language(String name){
		Node node = graphDb.createNode();
		node.setProperty(LANG_NAME,name);
	    node.setProperty(NODE_TYPE, LANG_NODE);
	    return node;
	} 

	private Node Series(String series_id, String title, String title_id,
			String tag_line, String feature_image, String keywords,
			String copyrights, String dd_img, String dd_summary,
			int series_type, int time_created, int series_price) {
		Node node = graphDb.createNode();
		node.setProperty(SERIES_ID, series_id);
		node.setProperty(SERIES_TITLE, title);
		node.setProperty(SERIES_TITLE_ID, title_id);
		node.setProperty(SERIES_TAG_LINE, tag_line);
		node.setProperty(SERIES_FEAT_IMG, feature_image);
		node.setProperty(SERIES_KEYWORDS, keywords);
		node.setProperty(SERIES_COPYRIGHTS, copyrights);
		node.setProperty(SERIES_DD_IMG, dd_img);
		node.setProperty(SERIES_DD_SUMMARY, dd_summary);
		node.setProperty(SERIES_TYPE, series_type);
		node.setProperty(NODE_TYPE, SERIES_NODE);
		node.setProperty(TIME_CREATED, time_created);
		node.setProperty(SERIES_PRICE, series_price);
		
		return node;
	}
	
	private Node Anthology(String anthology_id, String title, String title_id,
			String summary, String feature_image, int time_created) {
		Node node = graphDb.createNode();
		node.setProperty(ANTHOLOGY_ID, anthology_id);
		node.setProperty(ANTHOLOGY_TITLE, title);
		node.setProperty(ANTHOLOGY_TITLE_ID, title_id);
		node.setProperty(ANTHOLOGY_SUMMARY, summary);
		node.setProperty(ANTHOLOGY_FEAT_IMG, feature_image);
		node.setProperty(NODE_TYPE, ANTHOLOGY_NODE);
		node.setProperty(TIME_CREATED, time_created);
		
		return node;
	}
	
	private Node Nanostory(String nanostory_id, String content, String feature_image, int time_created) {
		Node node = graphDb.createNode();
		node.setProperty(NANOSTORY_ID, nanostory_id);
		node.setProperty(NANOSTORY_FEAT_IMG, feature_image);
		node.setProperty(NANOSTORY_CONTENT, content);
		node.setProperty(NANOSTORY_VIEWS,0);
		node.setProperty(NODE_TYPE, NANOSTORY_NODE);
		node.setProperty(TIME_CREATED, time_created);
		
		return node;
	}
	
	private Node Review(String review_id, String data, int time_created)
	{
		Node node = graphDb.createNode();
		node.setProperty(REVIEW_ID, review_id);
		node.setProperty(REVIEW_DATA, data);
		node.setProperty(NODE_TYPE, REVIEW_NODE);
		return node;
	}

	private Node Chapter(String chapter_id, String title_id, String title,
			String feat_image, int free_or_paid, int time_created, int w_count) {
		Node node = graphDb.createNode();
		node.setProperty(CHAPTER_ID, chapter_id);
		node.setProperty(CHAPTER_TITLE_ID, title_id);
		node.setProperty(CHAPTER_TITLE, title);
		node.setProperty(CHAPTER_FEAT_IMAGE, feat_image);
		node.setProperty(CHAPTER_FREE_OR_PAID, free_or_paid);
		node.setProperty(TIME_CREATED, time_created);
		node.setProperty(CHAPTER_WORDS_COUNT, w_count);
		node.setProperty(NODE_TYPE, CHAPTER_NODE);
		node.setProperty(TOTAL_CHAPTER_VIEWS, 0);
		node.setProperty(TOTAL_CHAPTER_READS, 0);
		node.setProperty(CHAPTER_LAST_VIEWED_TIME, 0);
		return node;
	}

	private Node Contest(String contest_id, String title, String title_id,
			String summary, String description, String feat_image,
			String award_info, String theme_info, String partners_info, int start_date,
			int end_date, String language, int price, int mode, int time_created, int type, int is_open) {
		Node node = graphDb.createNode();
		node.setProperty(CONTEST_ID, contest_id);
		node.setProperty(CONTEST_TITLE, title);
		node.setProperty(CONTEST_TITLE_ID, title_id);
		node.setProperty(CONTEST_SUMMARY, summary);
		node.setProperty(CONTEST_DESCRIPTION, description);
		node.setProperty(CONTEST_FEAT_IMAGE, feat_image);
		node.setProperty(CONTEST_AWARD_INFO, award_info);
		node.setProperty(CONTEST_THEME_INFO, theme_info);
		node.setProperty(CONTEST_PARTNERS_INFO, partners_info);
		node.setProperty(CONTEST_START_DATE, start_date);
		node.setProperty(CONTEST_END_DATE, end_date);
		node.setProperty(CONTEST_LANGUAGE, language);
		node.setProperty(CONTEST_PRICE, price);
		node.setProperty(CONTEST_TYPE, type);
		node.setProperty(CONTEST_MODE, mode);
		node.setProperty(CONTEST_VISIBILTY, is_open);
		node.setProperty(TIME_CREATED, time_created);
		node.setProperty(NODE_TYPE, CONTEST_NODE);
		return node;
	}
	
	public void startThriftServer()
	{
		//starting thrift server		
		try{
			TServerSocket serverTransport = new TServerSocket(9777);
			KahaniyaService.Processor<KahaniyaService.Iface> processor = new KahaniyaService.Processor<KahaniyaService.Iface>(this);
			Args serverArgs = new Args(serverTransport);
			serverArgs.processor(processor);
			TServer server = new TThreadPoolServer(serverArgs);
			System.out.println("Kahaniya thrift service is started");
			server.serve();
			
		}catch(Exception e)
		{
			System.out.println(new Date().toString());
			e.printStackTrace();
		}
	}
	
	@Override
	public String jar_shutdown()
	{
		String res;
		try{
			System.out.println("Got the request to shut_down");
			Timer timer  = new Timer();
			TimerTask timerTask = new TimerTask () {
				    @Override
				    public void run () {
				      System.out.println("Jar will terminate in 1 minute");
				    	System.exit(0);
				    }
				};
				timer.schedule (timerTask, 60000);
				res = "Jar will terminate in 1 minute";
		}
		catch(Exception ex){
			System.out.println(new Date().toString());
		      System.out.println("Something went wrong, while jar_shutdown :"+ex.getMessage());
		      ex.printStackTrace();
		      res = "Failed to terminate";
		   }
		return res;
	}
	
	public Kahaniya()
	{
		if(graphDb == null)
			initGraphDb();
	
	}

	@Override
	public String db_backup()
	{
		String res;
		try{
			System.out.println("Got the request to backup db");
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			String folderName = c.get(Calendar.YEAR)+"/"+c.get(Calendar.MONTH)+"/"+c.get(Calendar.DATE)+"/"+c.get(Calendar.HOUR_OF_DAY)+"/"+c.get(Calendar.MINUTE);
			File f = new File("/var/kahaniyaN4j/data/backup/"+folderName+"/graphdb");
			OnlineBackup backup = OnlineBackup.from( "127.0.0.1" );
			backup.backup(f.getPath(), true);
			res = "true";
		}
		catch(Exception ex){
			System.out.println(new Date().toString());
		      System.out.println("Something went wrong, while db_backup :"+ex.getMessage());
		      ex.printStackTrace();
		      res = "false";
		   }
		return res;
	}
	
	private static void initGraphDb()
	{
		//db path
		String storeDir = "/var/kahaniyaN4j/data/graph.db";
		
		//starting graph database with configuration
		graphDb = new GraphDatabaseFactory()
	    .newEmbeddedDatabaseBuilder( storeDir )
	    .setConfig(GraphDatabaseSettings.allow_store_upgrade, "true")
	    .setConfig(GraphDatabaseSettings.keep_logical_logs, "2 days")
	    .newGraphDatabase();
		
		//register safe shutdown while exiting
		registerShutdownHook( graphDb );
		
		//create indexes if any (node index and relation index)
		/*String[] nodeIndexNames = {
								GENRE_NAME_INDEX,
								LANG_NAME_INDEX,
								GENRE_LANG_INDEX,
								USER_ID_INDEX,
								USER_NAME_INDEX,
								USER_EMAIL_INDEX,
								COMMENT_ID_INDEX,
								SERIES_ID_INDEX,
								REVIEW_ID_INDEX,
								CHAPTER_ID_INDEX,
								CHAPTER_TITLE_ID_INDEX,
								SERIES_TITLE_ID_INDEX,
								SERIES_TYPE_INDEX,
								KEYWORD_INDEX,
								LOCK_INDEX,
								SEARCH_INDEX,
								WRITING_STYLE_NAME_INDEX
									};	
		
		
		String[] relationshipIndexNames = {
								USER_VIEWED_CHAPTER_REL_INDEX,
								USER_READ_CHAPTER_REL_INDEX
									};
									
		*/

		//create full text indexes for search
		String[] nodeIndexNames = {
								SEARCH_INDEX
									};	
		
		
		//customConfiguration for indexes
		Map<String, String> customConfig = new HashMap<String,String>();
		customConfig.put("provider", "lucene");
		customConfig.put("type", "fulltext");
		
		try (Transaction tx = graphDb.beginTx())
		{
			//create indexes for nodes
			for(String nodeIndexName: nodeIndexNames)
				graphDb.index().forNodes(nodeIndexName, customConfig);
			
			//create indexes for relations
/*			for(String relationIndexName: relationshipIndexNames)
				graphDb.index().forRelationships(relationIndexName, customConfig);
*/				
			tx.success();
		}
		catch(Exception e){
			System.out.println(new Date().toString());
			System.out.println("Failed to create indexes");
			e.printStackTrace();
			}
		finally{}
	}

	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
	
	public void add_neo4j_lock_nodes()
	{
		try (Transaction tx = graphDb.beginTx())
		{
			Index<Node> lockNodeIndex = graphDb.index().forNodes( LOCK_INDEX );
				
			//create lock node
			if(lockNodeIndex.get(LockName, LockName).getSingle() == null){ //create node if and only if there no locknode with given name
				Node lockNode = graphDb.createNode();  //creating a node
				lockNode.setProperty( LockName, LockName ); //attach name to lock node
				lockNode.setProperty( NODE_TYPE, LOCK_NODE ); //attach nod type to lock node
				lockNodeIndex.add( lockNode, LockName, LockName ); //attach node to lock node index to retrieve later
			}
			else{
				Node lockNode = lockNodeIndex.get(LockName, LockName).getSingle();
				if(!lockNode.hasProperty(NODE_TYPE))
					lockNode.setProperty(NODE_TYPE, LOCK_NODE);
			}

			//create series started node
			if(lockNodeIndex.get(SERIES_STATUS, SERIES_STARTED_BASE_NODE).getSingle() == null){ //create node if and only if there no locknode with given name
				Node series_started_node = graphDb.createNode();  //creating a node
				series_started_node.setProperty( SERIES_STATUS, SERIES_STARTED_BASE_NODE ); //attach name to lock node
				series_started_node.setProperty( NODE_TYPE, SERIES_STATUS_BASE_NODE ); //attach nod type to lock node
				lockNodeIndex.add( series_started_node, SERIES_STATUS,  SERIES_STARTED_BASE_NODE ); //attach node to lock node index to retrieve later
			}
			//create series started node
			if(lockNodeIndex.get(SERIES_STATUS, SERIES_ONGOING_BASE_NODE).getSingle() == null){ //create node if and only if there no locknode with given name
				Node series_ongoing_node = graphDb.createNode();  //creating a node
				series_ongoing_node.setProperty( SERIES_STATUS, SERIES_ONGOING_BASE_NODE ); //attach name to lock node
				series_ongoing_node.setProperty( NODE_TYPE, SERIES_STATUS_BASE_NODE ); //attach nod type to lock node
				lockNodeIndex.add( series_ongoing_node, SERIES_STATUS,  SERIES_ONGOING_BASE_NODE ); //attach node to lock node index to retrieve later
			}
			//create series started node
			if(lockNodeIndex.get(SERIES_STATUS, SERIES_COMPLETED_BASE_NODE).getSingle() == null){ //create node if and only if there no locknode with given name
				Node series_completed_node = graphDb.createNode();  //creating a node
				series_completed_node.setProperty( SERIES_STATUS, SERIES_COMPLETED_BASE_NODE ); //attach name to lock node
				series_completed_node.setProperty( NODE_TYPE, SERIES_STATUS_BASE_NODE ); //attach nod type to lock node
				lockNodeIndex.add( series_completed_node, SERIES_STATUS,  SERIES_COMPLETED_BASE_NODE ); //attach node to lock node index to retrieve later
			}
			tx.success();
		}
		catch(Exception e){
			System.out.println(new Date().toString());
			System.out.println("Exception @ add_neo4j_lock_nodes()");
			System.out.println("Failed to create lock nodes : " + e.getMessage());}
		finally{}
	}
	
	private Node getWritingStyleNode(String name)
	{
		Index<Node> writingStyleIndex = graphDb.index().forNodes( WRITING_STYLE_NAME_INDEX );
		if(writingStyleIndex.get(WRITING_STYLE_NAME, name.toLowerCase()).getSingle() == null){ //create node if and only if there no locknode with given name
			Node writingStyleNode = graphDb.createNode();  //creating a node
			writingStyleNode.setProperty( WRITING_STYLE_NAME, name ); //attach name to lock node
			writingStyleNode.setProperty( NODE_TYPE, name ); //attach nod type to lock node
			writingStyleIndex.add( writingStyleNode, WRITING_STYLE_NAME, name.toLowerCase() ); //attach node to lock node index to retrieve later
			return writingStyleNode;
		}
		return writingStyleIndex.get(WRITING_STYLE_NAME, name.toLowerCase()).getSingle();
	}
	
	public void add_default_price_to_series() 
	{
		try (Transaction tx = graphDb.beginTx())
		{
			Index<Node> seriesNodeIndex = graphDb.index().forNodes( SERIES_ID_INDEX );
						
			ResourceIterator<Node> seriesItr = seriesNodeIndex.query(SERIES_ID, "*").iterator();
			
			while(seriesItr.hasNext())
			{
				Node series = seriesItr.next();
				
				if(!series.hasProperty(SERIES_PRICE))
					series.setProperty(SERIES_PRICE, 0);
			}
			
			tx.success();
		}
		catch(Exception e){
			System.out.println(new Date().toString());
			System.out.println("Exception @ add_default_price_to_series()");
			System.out.println("Failed to add default price to series : " + e.getMessage());}
		finally{}
	}
	
	public void add_default_type_to_contest() 
	{
		try (Transaction tx = graphDb.beginTx())
		{
			Index<Node> contestNodeIndex = graphDb.index().forNodes( CONTEST_ID_INDEX );
			System.out.println("Adding Default type to contest");
						
			ResourceIterator<Node> contestItr = contestNodeIndex.query(CONTEST_ID, "*").iterator();
			
			while(contestItr.hasNext())
			{
				Node contest = contestItr.next();
				
				if(!contest.hasProperty(CONTEST_TYPE))
					contest.setProperty(CONTEST_TYPE, 3);
			}
			
			tx.success();
		}
		catch(Exception e){
			System.out.println(new Date().toString());
			System.out.println("Exception @ add_default_type_to_contest()");
			System.out.println("Failed to add_default_type_to_contest : " + e.getMessage());}
		finally{}
	}
	
	public void update_search_index() 
	{
		try (Transaction tx = graphDb.beginTx())
		{
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> anthologyId_index = graphDb.index().forNodes(ANTHOLOGY_ID_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			
						
			ResourceIterator<Node> chapterItr = chapterId_index.query(CHAPTER_ID, "*").iterator();
			System.out.println("updating chapter search index");
			while(chapterItr.hasNext())
			{
				Node chapterNode = chapterItr.next();
				
				search_index.remove(chapterNode);
				search_index.add(chapterNode, SEARCH_CHAPTER, 
						chapterNode.getProperty(CHAPTER_TITLE)+""+ chapterNode.getProperty(CHAPTER_TITLE_ID));
			}
			
			ResourceIterator<Node> seriesItr = seriesId_index.query(SERIES_ID, "*").iterator();
			System.out.println("updating series search index");
			while(seriesItr.hasNext())
			{
				Node seriesNode = seriesItr.next();
				
				search_index.remove(seriesNode);
				search_index.add(seriesNode, SEARCH_SERIES, 
						seriesNode.getProperty(SERIES_TITLE)+""+ seriesNode.getProperty(SERIES_TITLE_ID));
			}
			
			ResourceIterator<Node> anthologyItr = anthologyId_index.query(ANTHOLOGY_ID, "*").iterator();
			System.out.println("updating anthology search index");
			while(anthologyItr.hasNext())
			{
				Node anthologyNode = anthologyItr.next();
				
				search_index.remove(anthologyNode);
				search_index.add(anthologyNode, SEARCH_ANTHOLOGY, 
						anthologyNode.getProperty(ANTHOLOGY_TITLE)+""+ anthologyNode.getProperty(ANTHOLOGY_TITLE_ID));
			}
			
			System.out.println("succesfullly updated search index");
			tx.success();
		}
		catch(Exception e){
			System.out.println(new Date().toString());
			System.out.println("Exception @ add_default_type_to_contest()");
			System.out.println("Failed to add_default_type_to_contest : " + e.getMessage());}
		finally{}
	}



	
/*	public void add_default_status_to_series() 
	{
		try (Transaction tx = graphDb.beginTx())
		{
			Index<Node> seriesNodeIndex = graphDb.index().forNodes( SERIES_ID_INDEX );
			Index<Node> seriesStatusBaseNodeIndex = graphDb.index().forNodes( LOCK_INDEX );

			Node seriesOngoingNode = seriesStatusBaseNodeIndex.get(SERIES_STATUS, SERIES_ONGOING_BASE_NODE).getSingle();
			Node seriesCompletedNode = seriesStatusBaseNodeIndex.get(SERIES_STATUS, SERIES_COMPLETED_BASE_NODE).getSingle();
			
			ResourceIterator<Node> seriesItr = seriesNodeIndex.query(SERIES_ID, "*").iterator();
			
			while(seriesItr.hasNext())
			{
				Node series = seriesItr.next();

				if(!series.hasRelationship(SERIES_STARTED_REL_TO_BASE_NODE) && !series.hasRelationship(SERIES_ONGOING_REL_TO_BASE_NODE) && !series.hasRelationship(SERIES_COMPLETED_REL_TO_BASE_NODE))
				{
					if(series.getProperty(SERIES_TYPE).toString().equals("2")) // for web series
						createRelation(SERIES_ONGOING_REL_TO_BASE_NODE, series, seriesOngoingNode);
					else //for short series
						createRelation(SERIES_COMPLETED_REL_TO_BASE_NODE, series, seriesCompletedNode);
				}

			}
			
			tx.success();
		}
		catch(Exception e){
			System.out.println(new Date().toString());
			System.out.println("Exception @ add_default_status_to_series()");
			System.out.println("Failed to add default status to stories : " + e.getMessage());}
		finally{}
	}
*/
/*	public void add_additional_properties() 
	{
		try (Transaction tx = graphDb.beginTx())
		{
			Index<Node> chapterNodeIndex = graphDb.index().forNodes( CHAPTER_ID_INDEX );
			Index<Node> seriesNodeIndex = graphDb.index().forNodes( SERIES_ID_INDEX );
			Index<Node> genreIndex = graphDb.index().forNodes( GENRE_NAME_INDEX );
			
			Node poetryGenreNode = genreIndex.get(GENRE_NAME,"poetry").getSingle();
			if(poetryGenreNode == null)
				System.out.println("Genre POETRY doesnot exists, tagging all series & chapters to PROSE writing style");
			
			Node proseNode = getWritingStyleNode("prose");
			Node poetryNode = getWritingStyleNode("poetry");
			
			ResourceIterator<Node> chapterItr = chapterNodeIndex.query(CHAPTER_ID, "*").iterator();
			ResourceIterator<Node> seriesItr = seriesNodeIndex.query(SERIES_ID, "*").iterator();
			
			while(chapterItr.hasNext())
			{
				Node chapter = chapterItr.next();

				if(!chapter.hasRelationship(CHAPTER_BELONGS_TO_WRITING_STYLE))
				{
					//get genre name from each chapter series, then check if it poetry genre or not
					if(poetryGenreNode != null && poetryGenreNode.equals(chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode()))
					{
						//if yes, link the chapter to poetry node (writing style node)
						if(chapter.hasProperty(TIME_CREATED))
							createRelation(CHAPTER_BELONGS_TO_WRITING_STYLE, chapter, poetryNode, Integer.parseInt(chapter.getProperty(TIME_CREATED).toString()));
						else
							createRelation(CHAPTER_BELONGS_TO_WRITING_STYLE, chapter, poetryNode);
					}
					else{
						//otherwise link the chapter to prose node (writing style node)
						if(chapter.hasProperty(TIME_CREATED))
							createRelation(CHAPTER_BELONGS_TO_WRITING_STYLE, chapter, proseNode, Integer.parseInt(chapter.getProperty(TIME_CREATED).toString()));
						else
							createRelation(CHAPTER_BELONGS_TO_WRITING_STYLE, chapter, proseNode);
					}
				}
			}
				
			while(seriesItr.hasNext())
			{
				Node series = seriesItr.next();

				if(!series.hasRelationship(SERIES_BELONGS_TO_WRITING_STYLE))
				{
					//get genre name from each chapter series, then check if it poetry genre or not
					if(poetryGenreNode != null && poetryGenreNode.equals(series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode()))
					{
						//if yes, link the chapter to poetry node (writing style node)
						if(series.hasProperty(TIME_CREATED))
							createRelation(SERIES_BELONGS_TO_WRITING_STYLE, series, poetryNode, Integer.parseInt(series.getProperty(TIME_CREATED).toString()));
						else
							createRelation(SERIES_BELONGS_TO_WRITING_STYLE, series, poetryNode);
					}
					else{
						//otherwise link the chapter to prose node (writing style node)
						if(series.hasProperty(TIME_CREATED))
							createRelation(SERIES_BELONGS_TO_WRITING_STYLE, series, proseNode, Integer.parseInt(series.getProperty(TIME_CREATED).toString()));
						else
							createRelation(SERIES_BELONGS_TO_WRITING_STYLE, series, proseNode);
					}
				}

			}
			
			tx.success();
		}
		catch(Exception e){
			System.out.println(new Date().toString());
			System.out.println("Exception @ add_additional_properties()");
			System.out.println("Failed to add additional properties : " + e.getMessage());}
		finally{}
	}
*/	
/*	public void add_chapter_contest_status_properties() 
	{
		try (Transaction tx = graphDb.beginTx())
		{
			Index<Node> chapterNodeIndex = graphDb.index().forNodes( CHAPTER_ID_INDEX );
			
			ResourceIterator<Node> chapterItr = chapterNodeIndex.query(CHAPTER_ID, "*").iterator();
			
			while(chapterItr.hasNext())
			{
				Node chapter = chapterItr.next();

				if(chapter.hasRelationship(CHAPTER_BELONGS_TO_CONTEST))
				{
					Relationship rel = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_CONTEST, Direction.OUTGOING);
					if(!rel.hasProperty(CHAPTER_CONTEST_STATUS))
						rel.setProperty(CHAPTER_CONTEST_STATUS, 0);
				}
				
			}
			tx.success();
		}
		catch(Exception e){
			System.out.println(new Date().toString());
			System.out.println("Exception @ add_chapter_contest_status_properties()");
			System.out.println("Failed to add chapter contest status properties : " + e.getMessage());}
		finally{}
	}
*/	
	// Set default value to chapter_words_count property on chapters 
	public void add_chapter_words_count_property() 
	{
		try (Transaction tx = graphDb.beginTx())
		{
			Index<Node> chapterNodeIndex = graphDb.index().forNodes( CHAPTER_ID_INDEX );
			
			ResourceIterator<Node> chapterItr = chapterNodeIndex.query(CHAPTER_ID, "*").iterator();
			
			while(chapterItr.hasNext())
			{
				Node chapter = chapterItr.next();

				if(!chapter.hasProperty(CHAPTER_WORDS_COUNT))
				{
					chapter.setProperty(CHAPTER_WORDS_COUNT, 0);
				}
				
			}
			tx.success();
		}
		catch(Exception e){
			System.out.println(new Date().toString());
			System.out.println("Exception @ add_chapter_words_count_property()");
			System.out.println("Failed to add chapter words count property : " + e.getMessage());}
		finally{}
	}
	
	// Set default value to chapter_words_count property on chapters 
	public void add_contest_visibilty_property() 
	{
		try (Transaction tx = graphDb.beginTx())
		{
			Index<Node> chapterNodeIndex = graphDb.index().forNodes( CONTEST_ID_INDEX );
			
			ResourceIterator<Node> chapterItr = chapterNodeIndex.query(CONTEST_ID, "*").iterator();
			
			while(chapterItr.hasNext())
			{
				Node chapter = chapterItr.next();

				if(!chapter.hasProperty(CONTEST_VISIBILTY))
				{
					chapter.setProperty(CONTEST_VISIBILTY, 1);
					System.out.println("Updating...........");
				}
				
			}
			System.out.println("Succesfully Updated default Contest Visibility property");
			tx.success();
		}
		catch(Exception e){
			System.out.println(new Date().toString());
			System.out.println("Exception @ add_contest_visibilty_property()");
			System.out.println("Failed to add_contest_visibilty_property : " + e.getMessage());}
		finally{}
	}

	
	private Lock aquireWriteLock(Transaction tx) throws Exception {
		Index<Node> lockNodeIndex = graphDb.index().forNodes( LOCK_INDEX );
		Node tobeLockedNode = lockNodeIndex.get( LockName, LockName ).getSingle();
		if(tobeLockedNode == null)
	      throw new RuntimeException("Locking node for "+LockName+" not found, unbale to synchronize the call.");
		return tx.acquireWriteLock(tobeLockedNode);  //lock simultaneous execution of create_call to avoid duplicate creation
	}

	private boolean isRelationExistsBetween(RelationshipType relType, Node srcNode, Node targetNode)
	{
		if(srcNode == null || targetNode == null || relType == null)
			return false;
		
		Iterator<Relationship> itr = srcNode.getRelationships(Direction.OUTGOING, relType).iterator();
		while(itr.hasNext())
		{
			if(targetNode.equals(itr.next().getEndNode())) return true;
		}
		return false;
	}

	private void deleteRelation(RelationshipType relType, Node srcNode, Node targetNode)
	{
		Iterator<Relationship> itr = srcNode.getRelationships(Direction.OUTGOING, relType).iterator();
		while(itr.hasNext())
		{
			Relationship t_rel = itr.next();
			if(targetNode.equals(t_rel.getEndNode()))
			{
				t_rel.delete();
				return;
			}
		}
	}

	private Relationship createRelation(RelationshipType relType, Node srcNode, Node targetNode)
	{
		Relationship rel = srcNode.createRelationshipTo(targetNode, relType);
		rel.setProperty(TIME_CREATED, (int)(System.currentTimeMillis()/1000));
		return rel;
	}

	private Relationship createRelation(RelationshipType relType, Node srcNode, Node targetNode, int time_created)
	{
		Relationship rel = srcNode.createRelationshipTo(targetNode, relType);
		rel.setProperty(TIME_CREATED, time_created);
		return rel;
	}

	@Override
	public String create_genre(String name)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
			
			if(name == null || name.length() == 0)
				throw new KahaniyaCustomException("Null or empty string found for genre name");
			
			name = name.toLowerCase();
			
			if(genreName_index.get(GENRE_NAME,name).getSingle()!=null)
				throw new KahaniyaCustomException("Genre already exists with given name : "+name);
			
			Node genre_node = Genre(name);  // Creating a new genre node
			if(genre_node == null)
				throw new KahaniyaCustomException("Something went wrong, while creating genre with given name");

			//Indexing newly created genre node
			genreName_index.add(genre_node, GENRE_NAME, name.toLowerCase());
			
			//create genre_lang nodes for each language
			ResourceIterator<Node> langNodesItr = langName_index.query(LANG_NAME, "*").iterator();
			while(langNodesItr.hasNext())
			{
				Node langNode = langNodesItr.next();
				genre_lang_index.add(GenreLang(name, langNode.getProperty(LANG_NAME).toString()), GENRE_LANG_NAME, (name+" "+langNode.getProperty(LANG_NAME).toString()).toLowerCase());
			}
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_genre('"+name+"')");
			System.out.println("Something went wrong, while creating genre from create_genre  :"+ex.getMessage());
//			ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_genre('"+name+"')");
			System.out.println("Something went wrong, while creating genre from create_genre  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String edit_genre(String old_name, String new_name)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);

			if(old_name == null || old_name.length() == 0)
				throw new KahaniyaCustomException("Null or empty string found for genre existing name");
			if(new_name == null || new_name.length() == 0)
				throw new KahaniyaCustomException("Null or empty string found for new genre name");
			
			old_name = old_name.toLowerCase();
			new_name = new_name.toLowerCase();
						
			Node genre_node = genreName_index.get(GENRE_NAME,old_name.toLowerCase()).getSingle();
	
			if(genre_node == null)
				throw new KahaniyaCustomException("Genre doesnot exists with given old name");

			if(genreName_index.get(GENRE_NAME,new_name.toLowerCase()).getSingle() != null)
				throw new KahaniyaCustomException("Genre already exists with given new name");

			genre_node.setProperty(GENRE_NAME, old_name);
			//Update indexing for genre node
			genreName_index.remove(genre_node);
			genreName_index.add(genre_node, GENRE_NAME, new_name.toLowerCase());
			
			//update all genre_lang nodes
			//create genre_lang nodes for each language
			ResourceIterator<Node> langNodesItr = langName_index.query(LANG_NAME, "*").iterator();
			while(langNodesItr.hasNext())
			{
				Node langNode = langNodesItr.next();
				Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, (old_name + " " + langNode.getProperty(LANG_NAME).toString()).toLowerCase()).getSingle();
				if(genre_lang_node != null)
				{
					genre_lang_node.setProperty(GENRE_LANG_NAME, new_name + " " + langNode.getProperty(LANG_NAME).toString());
					genre_lang_index.remove(genre_lang_node);
					genre_lang_index.add(genre_lang_node, GENRE_LANG_NAME, (new_name+ " " + langNode.getProperty(LANG_NAME).toString()).toLowerCase());
				}
			}
			
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_genre('"+old_name+"','"+new_name+"')");
			System.out.println("Something went wrong, while editing genre from edit_genre  :"+ex.getMessage());
//			ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_genre('"+old_name+"','"+new_name+"')");
			System.out.println("Something went wrong, while editing genre from edit_genre  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}

	@Override
	public String delete_genre(String name)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
			
			if(name == null || name.length() == 0)
				throw new KahaniyaCustomException("Null or empty string found for genre name");
			
			name = name.toLowerCase();
			
			Node genre_node = genreName_index.get(GENRE_NAME,name.toLowerCase()).getSingle();
			
			if(genre_node == null)
				throw new KahaniyaCustomException("Genre node doesnot exists with given name");

			//Remove relationships for genre node
//			for(Relationship rel : genre_node.getRelationships())
//				rel.delete();

			if(genre_node.getDegree(SERIES_BELONGS_TO_GENRE) > 0)
				throw new KahaniyaCustomException("Genre node cannot be deleted, there exists some series related to given genre : " + name );
			
			//Remove genre_lang for all other lang
			ResourceIterator<Node> langNodesItr = langName_index.query(LANG_NAME, "*").iterator();
			while(langNodesItr.hasNext())
			{
				Node langNode = langNodesItr.next();
				Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, (name + " " + langNode.getProperty(LANG_NAME).toString()).toLowerCase()).getSingle();
				if(genre_lang_node != null)
				{
					genre_lang_index.remove(genre_lang_node);
					genre_lang_node.delete();
				}
			}

			
			//Remove indexing for genre node
			genreName_index.remove(genre_node);
			genre_node.delete();
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_genre('"+name+"')");
			System.out.println("Something went wrong, while deleting genre from delete_genre  :"+ex.getMessage());
//			ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_genre('"+name+"')");
			System.out.println("Something went wrong, while deleting genre from delete_genre  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String list_genres()
			throws TException {
		
		JSONArray res = new JSONArray();
		
		try(Transaction tx = graphDb.beginTx())
		{
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			
			ResourceIterator<Node> genre_nodes_itr = genreName_index.query(GENRE_NAME,"*").iterator();
			while(genre_nodes_itr.hasNext())
				res.put(getJSONForGenre(genre_nodes_itr.next()));
			
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ list_genres()");
			System.out.println("Something went wrong, while returning genres  :"+ex.getMessage());
//			ex.printStackTrace();

		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ list_genres()");
			System.out.println("Something went wrong, while returning genres  :"+ex.getMessage());
			ex.printStackTrace();
		}
		
		return res.toString();
	}

	@Override
	public String create_language(String name)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
			
			if(name == null || name.length() == 0)
				throw new KahaniyaCustomException("Null or empty string found for language name");
			
			name = name.toLowerCase();
			
			if(langName_index.get(LANG_NAME,name.toLowerCase()).getSingle()!=null)
				throw new KahaniyaCustomException("Lang already exists with given name : "+name);

			Node lang_node = Language(name);  // Creating a new lang node
			if(lang_node == null)
				throw new KahaniyaCustomException("Something went wrong, while creating language with given name");

			//create genre_lang nodes for each language
			ResourceIterator<Node> genreNodesItr = genreName_index.query(GENRE_NAME, "*").iterator();
			while(genreNodesItr.hasNext())
			{
				Node genreNode = genreNodesItr.next();
				genre_lang_index.add(GenreLang(genreNode.getProperty(GENRE_NAME).toString(), name), GENRE_LANG_NAME, (genreNode.getProperty(GENRE_NAME).toString()+" "+name).toLowerCase());
			}
			

			//Indexing newly created lang node
			langName_index.add(lang_node, LANG_NAME, name.toLowerCase());
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_language('"+name+"')");
			System.out.println("Something went wrong, while creating language from create_language  :"+ex.getMessage());
//			ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_language('"+name+"')");
			System.out.println("Something went wrong, while creating language from create_language  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String edit_language(String old_name, String new_name)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);

			if(old_name == null || old_name.length() == 0)
				throw new KahaniyaCustomException("Null or empty string found for old language name");
			if(new_name == null || new_name.length() == 0)
				throw new KahaniyaCustomException("Null or empty string found for new language name");
			
			old_name = old_name.toLowerCase();
			new_name = new_name.toLowerCase();
			
			Node lang_node = langName_index.get(LANG_NAME,old_name.toLowerCase()).getSingle();
			
			if(lang_node == null)
				throw new KahaniyaCustomException("Lang doesnot exists with given old name");

			if(langName_index.get(LANG_NAME,new_name.toLowerCase()).getSingle() != null)
				throw new KahaniyaCustomException("Lang already exists with given new name");
			
			lang_node.setProperty(LANG_NAME, old_name);
			
			//update all genre_lang nodes
			//create genre_lang nodes for each language
			ResourceIterator<Node> genreNodesItr = genreName_index.query(GENRE_NAME, "*").iterator();
			while(genreNodesItr.hasNext())
			{
				Node genreNode = genreNodesItr.next();
				Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, (genreNode.getProperty(GENRE_NAME).toString()+" "+old_name).toLowerCase()).getSingle();
				if(genre_lang_node != null)
				{
					genre_lang_node.setProperty(GENRE_LANG_NAME, genreNode.getProperty(GENRE_NAME).toString()+" "+new_name);
					genre_lang_index.remove(genre_lang_node);
					genre_lang_index.add(genre_lang_node, GENRE_LANG_NAME, (genreNode.getProperty(GENRE_NAME).toString()+" "+new_name).toLowerCase());
				}
			}

			
			//Update indexing for lang node
			
			langName_index.remove(lang_node);
			langName_index.add(lang_node, LANG_NAME, new_name.toLowerCase());
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_language('"+old_name+"','"+new_name+"')");
			System.out.println("Something went wrong, while editing language from edit_language  :"+ex.getMessage());
//			ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_language('"+old_name+"','"+new_name+"')");
			System.out.println("Something went wrong, while editing language from edit_language  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}

	@Override
	public String delete_language(String name)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);

			if(name == null || name.length() == 0)
				throw new KahaniyaCustomException("Null or empty string found for language name");
			
			name = name.toLowerCase();

			Node lang_node = langName_index.get(LANG_NAME,name.toLowerCase()).getSingle();
			
			if(lang_node == null)
				throw new KahaniyaCustomException("Lang node doesnot exists with given name");

			//Remove relationships for lang node
//			for(Relationship rel : lang_node.getRelationships())
//				rel.delete();
			if(lang_node.getDegree(SERIES_BELONGS_TO_LANGUAGE) > 0)
				throw new KahaniyaCustomException("Lang node cannot be deleted, there exists some series related to given lang : " + name );


			//Remove genre_lang for all other lang
			ResourceIterator<Node> genreNodesItr = genreName_index.query(GENRE_NAME, "*").iterator();
			while(genreNodesItr.hasNext())
			{
				Node genreNode = genreNodesItr.next();
				Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, (genreNode.getProperty(LANG_NAME).toString()+" "+name).toLowerCase()).getSingle();
				if(genre_lang_node != null)
				{
					genre_lang_index.remove(genre_lang_node);
					genre_lang_node.delete();
				}
			}
			
			//Remove indexing for lang node
			langName_index.remove(lang_node);
			lang_node.delete();
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_language('"+name+"')");
			System.out.println("Something went wrong, while deleting language from delete_language  :"+ex.getMessage());
//			ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_language('"+name+"')");
			System.out.println("Something went wrong, while deleting language from delete_language  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String list_languages()
			throws TException {
		
		JSONArray res = new JSONArray();
		
		try(Transaction tx = graphDb.beginTx())
		{
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			ResourceIterator<Node> lang_nodes_itr = langName_index.query(LANG_NAME,"*").iterator();
			while(lang_nodes_itr.hasNext())
				res.put(getJSONForLang(lang_nodes_itr.next()));
			
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ list_languages()");
			System.out.println("Something went wrong, while returning languages  :"+ex.getMessage());
//			ex.printStackTrace();

		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ list_languages()");
			System.out.println("Something went wrong, while returning languages  :"+ex.getMessage());
			ex.printStackTrace();
		}
		
		return res.toString();
	}
	
	@Override
	public String create_user(String id, String full_name, String user_name,
			String email, String mobile_number, String dial_code, String dob, String gender,
			String address, String bio, String genres, String languages,
			int privilege, int status, int time_created)
			throws TException {

		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			
			if(id == null || id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the parameter id");
			if(full_name == null || full_name.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the parameter full_name");
			if(email == null)
				email = "";
			if(mobile_number == null)
				mobile_number = "";
			if(dial_code == null)
				dial_code = "";
			if(dob == null)
				dob = "";
			if(gender == null)
				gender = "";
			if(address == null)
				address = "";
			if(bio == null)
				bio = "";
			if(genres == null)
				genres = "";
			if(languages == null)
				languages = "";
			
			
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> userName_index = graphDb.index().forNodes(USER_NAME_INDEX);
			Index<Node> userEmail_index = graphDb.index().forNodes(USER_EMAIL_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			if(userId_index.get(USER_ID,id).getSingle()!=null)
				throw new KahaniyaCustomException("User already exists with given id : "+id);
			if(userName_index.get(USER_NAME,user_name.toLowerCase()).getSingle()!=null)
				throw new KahaniyaCustomException("User already exists with given user_name : "+user_name);			
			
			if(!email.equals("") && userEmail_index.get(EMAIL,email.toLowerCase()).getSingle()!=null)
				throw new KahaniyaCustomException("User already exists with given email : "+email);
			
			Node user_node = User(id, full_name, user_name, email, mobile_number, dial_code, dob, gender, address, bio, privilege, status, time_created);  // Creating a new user node
			if(user_node == null)
				throw new KahaniyaCustomException("Something went wrong, while creating user ");

			//create relationships with Genres and Languages
			for(String genre_name : genres.split(","))
			{
				if(genreName_index.get(GENRE_NAME, genre_name.toLowerCase()).getSingle() != null)
					createRelation(USER_INTERESTED_GENRE, user_node, genreName_index.get(GENRE_NAME, genre_name.toLowerCase()).getSingle());
			}
			
			for(String lang_name : languages.split(","))
			{
				if(langName_index.get(LANG_NAME, lang_name.toLowerCase()).getSingle() != null)
					createRelation(USER_INTERESTED_LANGUAGE, user_node, langName_index.get(LANG_NAME, lang_name.toLowerCase()).getSingle());
			}
			
			//Indexing newly created user node
			userId_index.add(user_node, USER_ID, id);
			userName_index.add(user_node, USER_NAME, user_name.toLowerCase());
			search_index.add(user_node, SEARCH_USER, user_name.toLowerCase() + " " + full_name.toLowerCase());
			
			if(!email.equals(""))
				userEmail_index.add(user_node, EMAIL, email.toLowerCase());

			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_user()");
			System.out.println("Something went wrong, while creating user from create_user  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_user()");
			System.out.println("Something went wrong, while creating user from create_user  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	
	}

	@Override
	public String edit_user_basic_info(String id, String full_name,
			String gender, String dob) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(id == null || id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param id");
			if(full_name == null || full_name.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param full_name");
			if(gender == null)
				gender = "";
			if(dob == null)
				dob = "";
			
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Node user_node = userId_index.get(USER_ID,id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User does not exists with given id : "+id);
			
			user_node.setProperty(FULL_NAME, full_name);
			user_node.setProperty(GENDER, gender);
			user_node.setProperty(DOB, dob);
			
			search_index.remove(user_node);
			search_index.add(user_node, SEARCH_USER, user_node.getProperty(USER_NAME).toString().toLowerCase() + " " + full_name.toLowerCase());
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_basic_info()");
			System.out.println("Something went wrong, while editing user from edit_user_basic_info  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_basic_info()");
			System.out.println("Something went wrong, while editing user from edit_user_basic_info  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	
	}

	@Override
	public String edit_user_contact_details(String id, String email,
			String mobile_number) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(id == null || id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param id");
			if(email == null)
				email = "";
			if(mobile_number == null)
				mobile_number = "";

			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> userEmail_index = graphDb.index().forNodes(USER_EMAIL_INDEX);
			
			Node user_node = userId_index.get(USER_ID,id).getSingle();
			
			if(user_node == null)
				throw new KahaniyaCustomException("User does not exists with given id : "+id);
			
			
			if(!email.equals("") && userEmail_index.get(EMAIL,email.toLowerCase()).getSingle()!=null)
				throw new KahaniyaCustomException("User already exists with given email : "+email);
			
			
			user_node.setProperty(EMAIL, email);
			user_node.setProperty(MOBILE_NUMBER, mobile_number);
			
			userEmail_index.remove(user_node);
			if(!email.equals(""))userEmail_index.add(user_node, EMAIL, email.toLowerCase());
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_contact_details()");
			System.out.println("Something went wrong, while editing user from edit_user_contact_details  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_contact_details()");
			System.out.println("Something went wrong, while editing user from edit_user_contact_details  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String edit_user_status(String id, int status) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(id == null || id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param id");

			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node user_node = userId_index.get(USER_ID,id).getSingle();
			
			if(user_node == null)
				throw new KahaniyaCustomException("User does not exists with given id : "+id);
			
			// status - 0 : Deactivated 1: Regular 2: verified 3: ghost 
			user_node.setProperty(STATUS, status);
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_status()");
			System.out.println("Something went wrong, while editing user from edit_user_status  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_status()");
			System.out.println("Something went wrong, while editing user from edit_user_status  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String edit_user_security_details(String id, String user_name)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(id == null || id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param id");
			if(user_name == null || user_name.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_name");
			
			
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> userName_index = graphDb.index().forNodes(USER_NAME_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Node user_node = userId_index.get(USER_ID,id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User does not exists with given id : "+id);
			
			user_node.setProperty(USER_NAME, user_name);
			
			userName_index.remove(user_node);
			userName_index.add(user_node, USER_NAME, user_name.toLowerCase());
			search_index.remove(user_node);
			search_index.add(user_node, SEARCH_USER, user_name.toLowerCase() + " " + user_node.getProperty(FULL_NAME).toString().toLowerCase());
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_security_details()");
			System.out.println("Something went wrong, while editing user from edit_user_security_details  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_security_details()");
			System.out.println("Something went wrong, while editing user from edit_user_security_details  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String edit_user_languages(String id, String languages)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(id == null || id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param id");
			if(languages == null)
				languages = "";
			
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			Node user_node = userId_index.get(USER_ID,id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User does not exists with given id : "+id);
			
			for(Relationship rel : user_node.getRelationships(USER_INTERESTED_LANGUAGE))
				rel.delete();

			for(String lang_name : languages.split(","))
			{
				if(langName_index.get(LANG_NAME, lang_name.toLowerCase()).getSingle() != null)
					createRelation(USER_INTERESTED_LANGUAGE, user_node, langName_index.get(LANG_NAME, lang_name.toLowerCase()).getSingle());
			}

			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_languages()");
			System.out.println("Something went wrong, while editing user from edit_user_languages  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_languages()");
			System.out.println("Something went wrong, while editing user from edit_user_languages  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String edit_user_genres(String id, String genres)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(id == null || id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param id");
			if(genres == null)
				genres = "";
			
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			
			Node user_node = userId_index.get(USER_ID,id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User does not exists with given id : "+id);
			
			for(Relationship rel : user_node.getRelationships(USER_INTERESTED_GENRE))
				rel.delete();

			for(String lang_name : genres.split(","))
			{
				if(genreName_index.get(GENRE_NAME, lang_name.toLowerCase()).getSingle() != null)
					createRelation(USER_INTERESTED_GENRE, user_node, genreName_index.get(GENRE_NAME, lang_name.toLowerCase()).getSingle());
			}

			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_genres()");
			System.out.println("Something went wrong, while editing user from edit_user_genres  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_genres()");
			System.out.println("Something went wrong, while editing user from edit_user_genres  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}

	@Override
	public String follow_user(String user_id_1, String user_id_2, int time)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id_1 == null || user_id_1.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id_1");
			if(user_id_2 == null || user_id_2.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id_2");
			
			aquireWriteLock(tx);
			
			Index<Node> userIdIndex = graphDb.index().forNodes(USER_ID_INDEX);
			Node userOne = userIdIndex.get(USER_ID, user_id_1).getSingle();
			Node userTwo = userIdIndex.get(USER_ID, user_id_2).getSingle();

			if(userOne == null)
				throw new KahaniyaCustomException("User does not exists with given id : " + user_id_1);
			if(userTwo == null)
				throw new KahaniyaCustomException("User does not exists with given id : " + user_id_2);
			
			if(isRelationExistsBetween(USER_FOLLOW_USER, userOne, userTwo))
			{
				deleteRelation(USER_FOLLOW_USER, userOne, userTwo);
			}			
			else
			{
				createRelation(USER_FOLLOW_USER, userOne, userTwo, time);
			}

			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ follow_user()");
			System.out.println("Something went wrong, while following user from follow_user  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ follow_user()");
			System.out.println("Something went wrong, while following user from follow_user  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String skip_follow_user(String user_id_1, String user_id_2, int time)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id_1 == null || user_id_1.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id_1");
			if(user_id_2 == null || user_id_2.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id_2");
			
			aquireWriteLock(tx);
			
			Index<Node> userIdIndex = graphDb.index().forNodes(USER_ID_INDEX);
			Node userOne = userIdIndex.get(USER_ID, user_id_1).getSingle();
			Node userTwo = userIdIndex.get(USER_ID, user_id_2).getSingle();

			if(userOne == null)
				throw new KahaniyaCustomException("User does not exists with given id : " + user_id_1);
			if(userTwo == null)
				throw new KahaniyaCustomException("User does not exists with given id : " + user_id_2);
			
			if(isRelationExistsBetween(USER_SKIP_FOLLOW_USER, userOne, userTwo))
			{
				deleteRelation(USER_SKIP_FOLLOW_USER, userOne, userTwo);
			}			
			else
			{
				createRelation(USER_SKIP_FOLLOW_USER, userOne, userTwo, time);
			}

			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ follow_user()");
			System.out.println("Something went wrong, while skip a user from follow suggestion  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ follow_user()");
			System.out.println("Something went wrong, while skip a user from follow suggesion  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String deactivate_user(String user_id) throws TException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private JSONObject getJSONForDiscoveryRel(Relationship rel, Node req_user)
	{
		Node actor = rel.getStartNode();
		Node post = rel.getEndNode();
		
		JSONObject postJSON = new JSONObject();
		JSONObject actorJSON = new JSONObject();
				
		actorJSON.put("Actr_Id", actor.getProperty(USER_ID).toString());
		actorJSON.put("Actr_Nme", actor.getProperty(FULL_NAME).toString());
		
		JSONArray actors = new JSONArray();
		actors.put(actorJSON);
		
		
		
		if(rel.isType(USER_WRITTEN_A_CHAPTER))
		{ // this was removed from discovery
			postJSON = getJSONForChapter(post, req_user);
//			postJSON.put("N_Typ","C");
//			if(isRelationExistsBetween(USER_FOLLOW_USER, req_user, rel.getStartNode()))
//				postJSON.put("N_Tag","NCF");
//			else postJSON.put("N_Tag","NCS");
			postJSON.put("N_Actors",actors);
			
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
//			postJSON.put("N_Data", obj);
//			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));
			
//			postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
//			postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_FAV_CHAPTER))
		{
			postJSON = getJSONForChapter(post, req_user);
			postJSON.put("N_Actors",actors);
			
//			postJSON.put("N_Data", obj);
//			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));
			int tot = post.getDegree(USER_FAV_CHAPTER)+ post.getDegree(USER_RATED_A_CHAPTER)
					+ post.getDegree(USER_WRITTEN_A_COMMENT) + post.getDegree(USER_PURCHASED_A_CHAPTER);
					
			postJSON.put("Num_Actors", tot);
			//postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
			//postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_RATED_A_CHAPTER))
		{
			postJSON = getJSONForChapter(post, req_user);
//			postJSON.put("N_Typ","C");
//			postJSON.put("N_Tag","R");
			postJSON.put("N_Actors",actors);
			
			
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
//			postJSON.put("N_Data", obj);
//			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));
			int tot = post.getDegree(USER_FAV_CHAPTER)+ post.getDegree(USER_RATED_A_CHAPTER)
					+ post.getDegree(USER_WRITTEN_A_COMMENT) + post.getDegree(USER_PURCHASED_A_CHAPTER);
			
			postJSON.put("Num_Actors", tot);
			//postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
			//postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_WRITTEN_A_REVIEW))
		{
			Iterator<Relationship> rev_on_series = post.getRelationships(REVIEW_BELONGS_TO_SERIES).iterator();
			post = rev_on_series.next().getEndNode();

			postJSON = getJSONForSeries(post, req_user);
			
//			postJSON.put("N_Typ","S");
//			postJSON.put("N_Tag","RV");
			postJSON.put("N_Actors",actors);
			
			
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
//			postJSON.put("N_Data", obj);
//			postJSON.put("N_Lnk", "/s/"+obj.getString("P_Title_ID"));
			Iterator<Relationship> num_actors = post.getRelationships(REVIEW_BELONGS_TO_SERIES).iterator();
			LinkedList<Node> actorNodes = new LinkedList<Node>();
			while(num_actors.hasNext())
			{
				Relationship t_rel = num_actors.next().getStartNode().getSingleRelationship(USER_WRITTEN_A_REVIEW, Direction.INCOMING);
				if(!actorNodes.contains(t_rel.getStartNode()))
					actorNodes.addLast(t_rel.getStartNode());
			}
			postJSON.put("Num_Actors", actorNodes.size());
			//postJSON.put("N_Pst_Ttl",post.getProperty(SERIES_TITLE));
			//postJSON.put("N_Pst_Link",post.getProperty(SERIES_TITLE_ID));
		}
		else if(rel.isType(USER_SUBSCRIBED_TO_SERIES))
		{
			postJSON = getJSONForSeries(post, req_user);
//			postJSON.put("N_Typ","S");
//			postJSON.put("N_Tag","SUB");
			postJSON.put("N_Actors",actors);
			
			
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
//			postJSON.put("N_Data", obj);
//			postJSON.put("N_Lnk", "/s/"+obj.getString("P_Title_ID"));
			postJSON.put("Num_Actors", post.getDegree(USER_SUBSCRIBED_TO_SERIES));
			//postJSON.put("N_Pst_Ttl",post.getProperty(SERIES_TITLE));
			//postJSON.put("N_Pst_Link",post.getProperty(SERIES_TITLE_ID));
		}
		else if(rel.isType(USER_STARTED_SERIES))
		{
			postJSON = getJSONForSeries(post, req_user);
//			postJSON.put("N_Typ","S");
//			postJSON.put("N_Tag","W");
			postJSON.put("N_Actors",actors);
			
			
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
//			postJSON.put("N_Data", obj);
//			postJSON.put("N_Lnk", "/s/"+obj.getString("P_Title_ID"));
			//postJSON.put("N_Pst_Ttl",post.getProperty(SERIES_TITLE));
			//postJSON.put("N_Pst_Link",post.getProperty(SERIES_TITLE_ID));
		}
		else if(rel.isType(USER_WRITTEN_A_COMMENT))
		{
			Iterator<Relationship> cm_on_chapters = post.getRelationships(COMMENT_WRITTEN_ON_CHAPTER).iterator();
			post = cm_on_chapters.next().getEndNode();
			
			postJSON = getJSONForChapter(post, req_user);
			
//			postJSON.put("N_Typ","C");
//			postJSON.put("N_Tag","CM");
			postJSON.put("N_Actors",actors);
			
			
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
//			postJSON.put("N_Data", obj);
//			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));
//
			Iterator<Relationship> num_actors = post.getRelationships(COMMENT_WRITTEN_ON_CHAPTER).iterator();
			LinkedList<Node> actorNodes = new LinkedList<Node>();
			while(num_actors.hasNext())
			{
				Relationship t_rel = num_actors.next().getStartNode().getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING);
				if(!actorNodes.contains(t_rel.getStartNode()))
					actorNodes.addLast(t_rel.getStartNode());
			}
			postJSON.put("Num_Actors", actorNodes.size());
			
			//postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
			//postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_PURCHASED_A_CHAPTER))
		{
			postJSON = getJSONForChapter(post, req_user);
			
//			postJSON.put("N_Typ","C");
//			postJSON.put("N_Tag","PC");
			//postJSON.put("N_Actors",actors);
			
			
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
//			postJSON.put("N_Data", obj);
//			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));
			postJSON.put("Num_Actors", post.getDegree(USER_PURCHASED_A_CHAPTER));
			//postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
			//postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_READ_A_CHAPTER))
		{
			postJSON = getJSONForChapter(post, req_user);
			
//			postJSON.put("N_Typ","C");
//			postJSON.put("N_Tag","RD");
			//postJSON.put("N_Actors",actors);
			
			
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
//			postJSON.put("N_Data", obj);
//			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));
			postJSON.put("Num_Actors", post.getDegree(USER_READ_A_CHAPTER));
			//postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
			//postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_FOLLOW_USER))
		{
			postJSON = getJSONForUser(post, req_user);
//			postJSON.put("N_Typ","U");
//			postJSON.put("N_Tag","F");
			postJSON.put("N_Actors",actors);
			
			
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
//			postJSON.put("N_Data", obj);
//			postJSON.put("N_Lnk", "/"+obj.getString("U_Id"));
			postJSON.put("Num_Actors", post.getDegree(USER_FOLLOW_USER, Direction.INCOMING));
			//postJSON.put("N_Pst_Ttl",post.getProperty(FULL_NAME));
			//postJSON.put("N_Pst_Link",post.getProperty(USER_NAME));
		}
		
//		postJSON.put("N_Tm",rel.getProperty(TIME_CREATED));
		postJSON.put("Is_Neo4j",true);
		
		return postJSON;
	}


	private JSONObject getJSONForDiscoveryNotificationRel(Relationship rel, Node req_user)
	{
		Node actor = rel.getStartNode();
		Node post = rel.getEndNode();
		JSONObject postJSON = new JSONObject();
		
		JSONObject actorJSON = new JSONObject();
		actorJSON.put("Actr_Id", actor.getProperty(USER_ID).toString());
		actorJSON.put("Actr_Nme", actor.getProperty(FULL_NAME).toString());
		
		JSONArray actors = new JSONArray();
		actors.put(actorJSON);
		
		postJSON.put("N_Tm",rel.getProperty(TIME_CREATED));
		postJSON.put("Is_Neo4j",true);
		
		if(rel.isType(USER_WRITTEN_A_CHAPTER))
		{ // this was removed from discovery
			postJSON.put("N_Typ","C");
			if(isRelationExistsBetween(USER_FOLLOW_USER, req_user, rel.getStartNode()))
				postJSON.put("N_Tag","NCF");
			else postJSON.put("N_Tag","NCS");
			postJSON.put("N_Actors",actors);
			JSONObject obj = getJSONForChapter(post, req_user);
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
			postJSON.put("N_Data", obj);
			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));
			
//			postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
//			postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_FAV_CHAPTER))
		{
			postJSON.put("N_Typ","C");
			postJSON.put("N_Tag","FV");
			postJSON.put("N_Actors",actors);
			
			JSONObject obj = getJSONForChapter(post, req_user);
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
			postJSON.put("N_Data", obj);
			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));
			postJSON.put("Num_Actors", post.getDegree(USER_FAV_CHAPTER));
			//postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
			//postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_RATED_A_CHAPTER))
		{
			postJSON.put("N_Typ","C");
			postJSON.put("N_Tag","R");
			postJSON.put("N_Actors",actors);
			
			JSONObject obj = getJSONForChapter(post, req_user);
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
			postJSON.put("N_Data", obj);
			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));
			postJSON.put("Num_Actors", post.getDegree(USER_RATED_A_CHAPTER));
			//postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
			//postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_WRITTEN_A_REVIEW))
		{
			postJSON.put("N_Typ","S");
			postJSON.put("N_Tag","RV");
			postJSON.put("N_Actors",actors);
			
			Iterator<Relationship> rev_on_series = post.getRelationships(REVIEW_BELONGS_TO_SERIES).iterator();
			post = rev_on_series.next().getEndNode();

			JSONObject obj = getJSONForSeries(post, req_user);
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
			postJSON.put("N_Data", obj);
			postJSON.put("N_Lnk", "/s/"+obj.getString("P_Title_ID"));
			Iterator<Relationship> num_actors = post.getRelationships(REVIEW_BELONGS_TO_SERIES).iterator();
			LinkedList<Node> actorNodes = new LinkedList<Node>();
			while(num_actors.hasNext())
			{
				Relationship t_rel = num_actors.next().getStartNode().getSingleRelationship(USER_WRITTEN_A_REVIEW, Direction.INCOMING);
				if(!actorNodes.contains(t_rel.getStartNode()))
					actorNodes.addLast(t_rel.getStartNode());
			}
			postJSON.put("Num_Actors", actorNodes.size());
			//postJSON.put("N_Pst_Ttl",post.getProperty(SERIES_TITLE));
			//postJSON.put("N_Pst_Link",post.getProperty(SERIES_TITLE_ID));
		}
		else if(rel.isType(USER_SUBSCRIBED_TO_SERIES))
		{
			postJSON.put("N_Typ","S");
			postJSON.put("N_Tag","SUB");
			postJSON.put("N_Actors",actors);
			
			JSONObject obj = getJSONForSeries(post, req_user);
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
			postJSON.put("N_Data", obj);
			postJSON.put("N_Lnk", "/s/"+obj.getString("P_Title_ID"));
			postJSON.put("Num_Actors", post.getDegree(USER_SUBSCRIBED_TO_SERIES));
			//postJSON.put("N_Pst_Ttl",post.getProperty(SERIES_TITLE));
			//postJSON.put("N_Pst_Link",post.getProperty(SERIES_TITLE_ID));
		}
		else if(rel.isType(USER_STARTED_SERIES))
		{
			postJSON.put("N_Typ","S");
			postJSON.put("N_Tag","W");
			postJSON.put("N_Actors",actors);
			
			JSONObject obj = getJSONForSeries(post, req_user);
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
			postJSON.put("N_Data", obj);
			postJSON.put("N_Lnk", "/s/"+obj.getString("P_Title_ID"));
			//postJSON.put("N_Pst_Ttl",post.getProperty(SERIES_TITLE));
			//postJSON.put("N_Pst_Link",post.getProperty(SERIES_TITLE_ID));
		}
		else if(rel.isType(USER_WRITTEN_A_COMMENT))
		{
			postJSON.put("N_Typ","C");
			postJSON.put("N_Tag","CM");
			postJSON.put("N_Actors",actors);
			
			Iterator<Relationship> cm_on_chapters = post.getRelationships(COMMENT_WRITTEN_ON_CHAPTER).iterator();
			post = cm_on_chapters.next().getEndNode();
			
			JSONObject obj = getJSONForChapter(post, req_user);
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
			postJSON.put("N_Data", obj);
			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));

			Iterator<Relationship> num_actors = post.getRelationships(COMMENT_WRITTEN_ON_CHAPTER).iterator();
			LinkedList<Node> actorNodes = new LinkedList<Node>();
			while(num_actors.hasNext())
			{
				Relationship t_rel = num_actors.next().getStartNode().getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING);
				if(!actorNodes.contains(t_rel.getStartNode()))
					actorNodes.addLast(t_rel.getStartNode());
			}
			postJSON.put("Num_Actors", actorNodes.size());
			
			//postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
			//postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_PURCHASED_A_CHAPTER))
		{
			postJSON.put("N_Typ","C");
			postJSON.put("N_Tag","PC");
			//postJSON.put("N_Actors",actors);
			
			JSONObject obj = getJSONForChapter(post, req_user);
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
			postJSON.put("N_Data", obj);
			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));
			postJSON.put("Num_Actors", post.getDegree(USER_PURCHASED_A_CHAPTER));
			//postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
			//postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_READ_A_CHAPTER))
		{
			postJSON.put("N_Typ","C");
			postJSON.put("N_Tag","RD");
			//postJSON.put("N_Actors",actors);
			
			JSONObject obj = getJSONForChapter(post, req_user);
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
			postJSON.put("N_Data", obj);
			postJSON.put("N_Lnk", "/s/"+obj.getJSONObject("Series_Info").getString("Series_Tid")+"/"+obj.getString("P_Title_ID"));
			postJSON.put("Num_Actors", post.getDegree(USER_READ_A_CHAPTER));
			//postJSON.put("N_Pst_Ttl",post.getProperty(CHAPTER_TITLE));
			//postJSON.put("N_Pst_Link",post.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID)+"/"+post.getProperty(CHAPTER_TITLE_ID));
		}
		else if(rel.isType(USER_FOLLOW_USER))
		{
			postJSON.put("N_Typ","U");
			postJSON.put("N_Tag","F");
			postJSON.put("N_Actors",actors);
			
			JSONObject obj = getJSONForUser(post, req_user);
/*			Iterator<String> keys = obj.keys();
			while(keys.hasNext())
			{
				String key = keys.next();
				postJSON.put(key, obj.get(key));
			}
*/
			postJSON.put("N_Data", obj);
			postJSON.put("N_Lnk", "/"+obj.getString("U_Id"));
			postJSON.put("Num_Actors", post.getDegree(USER_FOLLOW_USER, Direction.INCOMING));
			//postJSON.put("N_Pst_Ttl",post.getProperty(FULL_NAME));
			//postJSON.put("N_Pst_Link",post.getProperty(USER_NAME));
		}
		
		return postJSON;
	}
	
	private JSONObject getJSONForGenre(Node genre)
	{
		JSONObject ret = new JSONObject();
		
			JSONObject obj = new JSONObject();
			obj.put("name", genre.getProperty(GENRE_NAME).toString());
		ret.put("type", "genre");
		ret.put("Obj",obj);
		ret.put("Is_Neo4j",true);
		return ret;
	}

	private JSONObject getJSONForReview(Node review)
	{
		JSONObject ret = new JSONObject();
		ret.put("id", review.getProperty(REVIEW_ID).toString());
		ret.put("data",review.getProperty(REVIEW_DATA));
		ret.put("Is_Neo4j",true);
		return ret;
	}
	
	private JSONObject getJSONForLang(Node genre)
	{
		JSONObject ret = new JSONObject();
		
			JSONObject obj = new JSONObject();
			obj.put("name", genre.getProperty(LANG_NAME).toString());
		ret.put("type", "lang");
		ret.put("Obj",obj);
		ret.put("Is_Neo4j",true);
		return ret;
	}
	
	@Override
	public String create_or_edit_nanostory(String nanostory_id, String user_id, String content, String genres, String language, 
			String feature_image, int time_created, int is_edit, String contestId)
			throws TException {
		if(is_edit == 0)
			return create_nanostory(nanostory_id, user_id, content, genres, language, feature_image, time_created,contestId);
		else if(is_edit == 1)
			return edit_nanostory(nanostory_id, user_id, content, genres, language, feature_image, time_created, contestId );
		else return "false";
	}
	
	private String create_nanostory(String nanostory_id, 
			String user_id, String content, String genres, String language, String feature_image, 
			int time_created, String contestId){

		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(nanostory_id == null || nanostory_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param nanostory_id");
			if(genres == null || genres.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param genre");
			if(language == null || language.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param language");
			if(feature_image == null)
				feature_image = "";
			if(content == null)
				content = "";
					
			
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> nanostoryId_index = graphDb.index().forNodes(NANOSTORY_ID_INDEX);
						
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
								
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
							
			Node userNode = userId_index.get(USER_ID,user_id).getSingle();
			if(userNode == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			if(nanostoryId_index.get(NANOSTORY_ID, nanostory_id).getSingle()!=null)
				throw new KahaniyaCustomException("Nanostory already exists with given id : "+nanostory_id);
							
			Node langNode = langName_index.get(LANG_NAME, language.toLowerCase()).getSingle();
			if(langNode == null)
				throw new KahaniyaCustomException("Language doesnot exists for the name : " + language);
					
			Node nanostory_node = Nanostory(nanostory_id, content, feature_image, time_created);  // Creating a new nanostory node
			if(nanostory_node == null)
				throw new KahaniyaCustomException("Something went wrong, while creating anthology ");

			//create relationship with user
			createRelation(USER_WRITTEN_A_NANOSTORY, userNode, nanostory_node);
			
			//create relationship with Genres
			genres = genres.toLowerCase();
			for(String genre : genres.split(","))
			{
				Node genreNode = genreName_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
				if(genreNode == null)
					throw new KahaniyaCustomException("Genre doesnot exists for the name : " + genre);
				
				createRelation(NANOSTORY_BELONGS_TO_GENRE, nanostory_node, genreNode);
				
				//Genre + Language Relation
				Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, genre.toLowerCase() + " " +language.toLowerCase()).getSingle();
				if(genre_lang_node == null)
					throw new KahaniyaCustomException("Genre + Language doesnot exists for the name : " + genre+" "+language);
				
				createRelation(NANOSTORY_BELONGS_TO_GENRE_LANGUAGE, nanostory_node, genre_lang_node);
				
			}
			
			// Add nanostory to contest
			if(contestId != null && contestId.length() != 0)
			{
				Index<Node> contestIdIndex = graphDb.index().forNodes(CONTEST_ID_INDEX);
				Node contestNode = contestIdIndex.get(CONTEST_ID, contestId).getSingle();
				if(contestNode != null)
					createRelation(NANOSTORY_BELONGS_TO_CONTEST, nanostory_node, contestNode);
				else
					throw new KahaniyaCustomException("Contest not found for the given contest id:"+contestId);
				nanostory_node.getSingleRelationship(NANOSTORY_BELONGS_TO_CONTEST, Direction.OUTGOING).setProperty(NANOSTORY_CONTEST_STATUS, 0);

			}

			
						
			//create relationships with Genres and Languages
			createRelation(NANOSTORY_BELONGS_TO_LANGUAGE, nanostory_node, langNode);
			
				
			//Indexing newly created anthology node
			nanostoryId_index.add(nanostory_node, NANOSTORY_ID, nanostory_id);
			
			search_index.add(nanostory_node, SEARCH_NANOSTORY, content.toLowerCase());
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_nanostory()");
			System.out.println("Something went wrong, while creating nanostory from create_nanostory  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_nanostory()");
			System.out.println("Something went wrong, while creating nanostory from create_nanostory  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;

	}

	private String edit_nanostory(String nanostory_id,
			String user_id, String content, String genres, String language, String feature_image, 
			int time_created, String contestId){

		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(nanostory_id == null || nanostory_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param nanostory_id");
			if(genres == null || genres.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param genre");
			if(language == null || language.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param language");
			if(content == null)
				content = "";
			if(feature_image == null)
				feature_image = "";
						
			aquireWriteLock(tx);
			Index<Node> nanostoryId_index = graphDb.index().forNodes(NANOSTORY_ID_INDEX);
						
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
					
			Node nanostory_node = nanostoryId_index.get(NANOSTORY_ID, nanostory_id).getSingle();
			if(nanostory_node == null)
				throw new KahaniyaCustomException("Anthology doesnot exists with given id : "+nanostory_id);
			
//			Node genreNode = genreName_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
//			if(genreNode == null)
//				throw new KahaniyaCustomException("Genre doesnot exists for the name : " + genre);
			
			Node langNode = langName_index.get(LANG_NAME, language.toLowerCase()).getSingle();
			if(langNode == null)
				throw new KahaniyaCustomException("Language doesnot exists for the name : " + language);
			
//			Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, genre.toLowerCase()+" "+language.toLowerCase()).getSingle();
//			if(genre_lang_node == null)
//				throw new KahaniyaCustomException("Genre Language doesnot exists for the name : " + genre+" "+language);
			
			//remove existing relationships with Genres, Languages and keywords
			for(Relationship rel : nanostory_node.getRelationships(NANOSTORY_BELONGS_TO_GENRE))
				rel.delete();
			for(Relationship rel : nanostory_node.getRelationships(NANOSTORY_BELONGS_TO_LANGUAGE))
				rel.delete();
			for(Relationship rel : nanostory_node.getRelationships(NANOSTORY_BELONGS_TO_GENRE_LANGUAGE))
				rel.delete();
			
			//create relationship with Genres
			genres = genres.toLowerCase();
			for(String genre : genres.split(","))
			{
				Node genreNode = genreName_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
				if(genreNode == null)
					throw new KahaniyaCustomException("Genre doesnot exists for the name : " + genre);
				
				createRelation(NANOSTORY_BELONGS_TO_GENRE, nanostory_node, genreNode);
				
				//Genre + Language Relation
				Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, genre.toLowerCase() + " " +language.toLowerCase()).getSingle();
				if(genre_lang_node == null)
					throw new KahaniyaCustomException("Genre + Language doesnot exists for the name : " + genre+" "+language);
				
				createRelation(NANOSTORY_BELONGS_TO_GENRE_LANGUAGE, nanostory_node, genre_lang_node);
				
			}
			
			// If nanostory not tagged to contest, adding nanostory to contest
			if(contestId != null && contestId.length() != 0)
			{
				Index<Node> contestIdIndex = graphDb.index().forNodes(CONTEST_ID_INDEX);
				Node contestNode = contestIdIndex.get(CONTEST_ID, contestId).getSingle();
				if(contestNode != null){
					if(!isRelationExistsBetween(NANOSTORY_BELONGS_TO_CONTEST, nanostory_node, contestNode)){
						Relationship rel = createRelation(NANOSTORY_BELONGS_TO_CONTEST, nanostory_node, contestNode);
						rel.setProperty(NANOSTORY_CONTEST_STATUS, 0);
						}
					}
				else
					throw new KahaniyaCustomException("Contest not found for the given contest id:"+contestId);
			}
			
						
			//create relationships with Genres and Languages
			createRelation(NANOSTORY_BELONGS_TO_LANGUAGE, nanostory_node, langNode);
			
			nanostory_node.setProperty(NANOSTORY_FEAT_IMG, feature_image);
			nanostory_node.setProperty(NANOSTORY_CONTENT, content);
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_nanostory()");
			System.out.println("Something went wrong, while editing nanostory from edit_nanostory  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_nanostory()");
			System.out.println("Something went wrong, while editing nanostory from edit_nanostory  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;

	}

	
	@Override
	public String create_or_edit_anthology(String anthology_id, String title, String title_id, 
			String summary, String feature_image, String user_id, String genres, String language,
			int time_created, int is_edit)
			throws TException {
		if(is_edit == 0)
			return create_anthology(anthology_id, title, title_id, summary, feature_image, user_id, genres, language, time_created);
		else if(is_edit == 1)
			return edit_anthology(anthology_id, title, title_id, summary, feature_image, user_id, genres, language, time_created);
		else return "false";
	}
	
	private String create_anthology(String anthology_id, 
			String title, String title_id, String summary,
			String feature_image, String user_id, String genres, String language,
			int time_created){

		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(anthology_id == null || anthology_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param anthology_id");
			if(title == null || title.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title");
			if(title_id == null || title_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title_id");
			if(genres == null || genres.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param genre");
			if(language == null || language.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param language");
			if(summary == null)
				summary = "";
			if(feature_image == null)
				feature_image = "";
					
			
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> anthologyId_index = graphDb.index().forNodes(ANTHOLOGY_ID_INDEX);
			Index<Node> anthologyTitleId_index = graphDb.index().forNodes(ANTHOLOGY_TITLE_ID_INDEX);
			
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
								
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
							
			Node userNode = userId_index.get(USER_ID,user_id).getSingle();
			if(userNode == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			if(anthologyId_index.get(ANTHOLOGY_ID,anthology_id).getSingle()!=null)
				throw new KahaniyaCustomException("Anthology already exists with given id : "+anthology_id);
			if(anthologyTitleId_index.get(ANTHOLOGY_TITLE_ID,title_id.toLowerCase()).getSingle()!=null)
				throw new KahaniyaCustomException("Anthology already exists with given title id : "+title_id);
				
			Node langNode = langName_index.get(LANG_NAME, language.toLowerCase()).getSingle();
			if(langNode == null)
				throw new KahaniyaCustomException("Language doesnot exists for the name : " + language);
					
			Node anthology_node = Anthology(anthology_id, title, title_id, summary, feature_image, time_created);  // Creating a new anthology node
			if(anthology_node == null)
				throw new KahaniyaCustomException("Something went wrong, while creating anthology ");

			//create relationship with user
			createRelation(USER_STARTED_ANTHOLOGY, userNode, anthology_node);
			
			//create relationship with Genres
			genres = genres.toLowerCase();
			for(String genre : genres.split(","))
			{
				Node genreNode = genreName_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
				if(genreNode == null)
					throw new KahaniyaCustomException("Genre doesnot exists for the name : " + genre);
				
				createRelation(ANTHOLOGY_BELONGS_TO_GENRE, anthology_node, genreNode);
				
				//Genre + Language Relation
				Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, genre.toLowerCase() + " " +language.toLowerCase()).getSingle();
				if(genre_lang_node == null)
					throw new KahaniyaCustomException("Genre + Language doesnot exists for the name : " + genre+" "+language);
				
				createRelation(ANTHOLOGY_BELONGS_TO_GENRE_LANGUAGE, anthology_node, genre_lang_node);
				
			}
			
						
			//create relationships with Genres and Languages
			createRelation(ANTHOLOGY_BELONGS_TO_LANGUAGE, anthology_node, langNode);
			
				
			//Indexing newly created anthology node
			anthologyId_index.add(anthology_node, ANTHOLOGY_ID, anthology_id);
			anthologyTitleId_index.add(anthology_node, ANTHOLOGY_TITLE_ID, title_id.toLowerCase());
			
			search_index.add(anthology_node, SEARCH_ANTHOLOGY, title +""+ title_id.toLowerCase());
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_anthology()");
			System.out.println("Something went wrong, while creating anthology from create_anthology  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_anthology()");
			System.out.println("Something went wrong, while creating anthology from create_anthology  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;

	}

	private String edit_anthology(String anthology_id, 
			String title, String title_id, String summary,
			String feature_image, String user_id, String genres, String language,
			int time_created){

		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(anthology_id == null || anthology_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param anthology_id");
			if(title == null || title.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title");
			if(title_id == null || title_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title_id");
			if(genres == null || genres.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param genre");
			if(language == null || language.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param language");
			if(summary == null)
				summary = "";
			if(feature_image == null)
				feature_image = "";
						
			aquireWriteLock(tx);
			Index<Node> anthologyId_index = graphDb.index().forNodes(ANTHOLOGY_ID_INDEX);
						
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
					
			Node anthology_node = anthologyId_index.get(ANTHOLOGY_ID, anthology_id).getSingle();
			if(anthology_node == null)
				throw new KahaniyaCustomException("Anthology doesnot exists with given id : "+anthology_id);
			
//			Node genreNode = genreName_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
//			if(genreNode == null)
//				throw new KahaniyaCustomException("Genre doesnot exists for the name : " + genre);
			
			Node langNode = langName_index.get(LANG_NAME, language.toLowerCase()).getSingle();
			if(langNode == null)
				throw new KahaniyaCustomException("Language doesnot exists for the name : " + language);
			
//			Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, genre.toLowerCase()+" "+language.toLowerCase()).getSingle();
//			if(genre_lang_node == null)
//				throw new KahaniyaCustomException("Genre Language doesnot exists for the name : " + genre+" "+language);
			
			//remove existing relationships with Genres, Languages and keywords
			for(Relationship rel : anthology_node.getRelationships(ANTHOLOGY_BELONGS_TO_GENRE))
				rel.delete();
			for(Relationship rel : anthology_node.getRelationships(ANTHOLOGY_BELONGS_TO_LANGUAGE))
				rel.delete();
			for(Relationship rel : anthology_node.getRelationships(ANTHOLOGY_BELONGS_TO_GENRE_LANGUAGE))
				rel.delete();
			
			//create relationship with Genres
			genres = genres.toLowerCase();
			for(String genre : genres.split(","))
			{
				Node genreNode = genreName_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
				if(genreNode == null)
					throw new KahaniyaCustomException("Genre doesnot exists for the name : " + genre);
				
				createRelation(ANTHOLOGY_BELONGS_TO_GENRE, anthology_node, genreNode);
				
				//Genre + Language Relation
				Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, genre.toLowerCase() + " " +language.toLowerCase()).getSingle();
				if(genre_lang_node == null)
					throw new KahaniyaCustomException("Genre + Language doesnot exists for the name : " + genre+" "+language);
				
				createRelation(ANTHOLOGY_BELONGS_TO_GENRE_LANGUAGE, anthology_node, genre_lang_node);
				
			}
			
						
			//create relationships with Genres and Languages
			createRelation(ANTHOLOGY_BELONGS_TO_LANGUAGE, anthology_node, langNode);
			
			anthology_node.setProperty(ANTHOLOGY_TITLE, title);
			anthology_node.setProperty(ANTHOLOGY_SUMMARY, summary);
			anthology_node.setProperty(ANTHOLOGY_FEAT_IMG, feature_image);
			
			search_index.remove(anthology_node);
			search_index.add(anthology_node, SEARCH_ANTHOLOGY, title +""+ title_id.toLowerCase());
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_anthology()");
			System.out.println("Something went wrong, while editing anthology from edit_anthology  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_anthology()");
			System.out.println("Something went wrong, while editing anthology from edit_anthology  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;

	}
	
	@Override
	public String tag_a_post(String anthology_id, String type_id, String type, String user_id, int time, int is_tag)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(anthology_id == null || anthology_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param anthology_id");

			aquireWriteLock(tx);
			Index<Node> anthologyId_index = graphDb.index().forNodes(ANTHOLOGY_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node anthology_node = anthologyId_index.get(ANTHOLOGY_ID, anthology_id).getSingle();
			if(anthology_node == null)
				throw new KahaniyaCustomException("Anthology doesnot exists with given id : "+anthology_id);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(type.equalsIgnoreCase("C")){
				Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
				Node chapter_node = chapterId_index.get(CHAPTER_ID, type_id).getSingle();
				if(chapter_node == null)
					throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+type_id);
				
				if(is_tag == 1){
					if(!isRelationExistsBetween(ANTHOLOGY_TAGGED_A_POST, anthology_node, chapter_node))
						createRelation(ANTHOLOGY_TAGGED_A_POST, anthology_node, chapter_node, time);
					else
						throw new KahaniyaCustomException("Already the given chapter tagged to anthology : "+type_id);
				}
				else if(is_tag == 0){					
					deleteRelation(ANTHOLOGY_TAGGED_A_POST, anthology_node, chapter_node);
				}
				else return "false";
				
			}else if(type.equalsIgnoreCase("S")){
				Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
				Node series_node = seriesId_index.get(SERIES_ID, type_id).getSingle();
				if(series_node == null)
					throw new KahaniyaCustomException("Series doesnot exists with given id : "+type_id);
				
				if(!isRelationExistsBetween(ANTHOLOGY_TAGGED_A_POST, anthology_node, series_node))
					createRelation(ANTHOLOGY_TAGGED_A_POST, anthology_node, series_node, time );
				else
					throw new KahaniyaCustomException("Already the given series tagged to anthology : "+type_id);
			}
									
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ tag_a_post()");
			System.out.println("Something went wrong, while anthology tagging a post  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ tag_a_post()");
			System.out.println("Something went wrong, while anthology tagging a post  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}



	@Override
	public String create_or_edit_series(String series_id, String user_id,
			String title, String title_id, String tag_line,
			String feature_image, String genre, String language,
			String keywords, String copyrights, String dd_img,
			String dd_summary, int series_type, int time_created, 
			int is_edit, String writing_style, String contestId, int series_price)
			throws TException {
		if(is_edit == 0)
			return create_series(series_id, user_id, title, title_id, tag_line, feature_image, genre, language, keywords, copyrights, dd_img, dd_summary, series_type, time_created, writing_style, contestId, series_price);
		else if(is_edit == 1)
			return edit_series(series_id, user_id, title, title_id, tag_line, feature_image, genre, language, keywords, copyrights, dd_img, dd_summary, series_type, time_created, contestId, series_price);
		else return "false";
	}
	
	private String create_series(String series_id, String user_id,
			String title, String title_id, String tag_line,
			String feature_image, String genre, String language,
			String keywords, String copyrights, String dd_img,
			String dd_summary, int series_type, int time_created,
			String writing_style, String contestId, int series_price){

		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			if(title == null || title.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title");
			if(title_id == null || title_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title_id");
			if(genre == null || genre.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param genre");
			if(language == null || language.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param language");
			if(tag_line == null)
				tag_line = "";
			if(feature_image == null)
				feature_image = "";
			if(keywords == null)
				keywords = "";
			if(copyrights == null)
				copyrights = "";
			if(dd_img == null)
				dd_img = "";
			if(dd_summary == null)
				dd_summary = "";
			if(writing_style == null)
				writing_style = "prose";
			else
				writing_style = writing_style.toLowerCase();
			
			
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> seriesTitleId_index = graphDb.index().forNodes(SERIES_TITLE_ID_INDEX);
			Index<Node> seriesType_index = graphDb.index().forNodes(SERIES_TYPE_INDEX);

			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Index<Node> series_status_base_node_index = graphDb.index().forNodes(LOCK_INDEX);
			Node seriesStartedBaseNode = series_status_base_node_index.get(SERIES_STATUS, SERIES_STARTED_BASE_NODE).getSingle();
			Node seriesCompletedBaseNode = series_status_base_node_index.get(SERIES_STATUS, SERIES_COMPLETED_BASE_NODE).getSingle();
			if(seriesStartedBaseNode == null)
				throw new KahaniyaCustomException("Unable to find SERIES_STARTED_BASE_NODE");
			if(seriesCompletedBaseNode == null)
				throw new KahaniyaCustomException("Unable to find SERIES_COMPLETED_BASE_NODE");
			
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
			
			Index<Node> keyword_index = graphDb.index().forNodes(KEYWORD_INDEX);
			
			Node userNode = userId_index.get(USER_ID,user_id).getSingle();
			if(userNode == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			if(seriesId_index.get(SERIES_ID,series_id).getSingle()!=null)
				throw new KahaniyaCustomException("Series already exists with given id : "+series_id);
			if(seriesTitleId_index.get(SERIES_TITLE_ID,title_id.toLowerCase()).getSingle()!=null)
				throw new KahaniyaCustomException("Series already exists with given title id : "+title_id);

			Node genreNode = genreName_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
			if(genreNode == null)
				throw new KahaniyaCustomException("Genre doesnot exists for the name : " + genre);
			
			Node langNode = langName_index.get(LANG_NAME, language.toLowerCase()).getSingle();
			if(langNode == null)
				throw new KahaniyaCustomException("Language doesnot exists for the name : " + language);
	
			Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, genre.toLowerCase() + " " +language.toLowerCase()).getSingle();
			if(genre_lang_node == null)
				throw new KahaniyaCustomException("Genre + Language doesnot exists for the name : " + genre+" "+language);
			
			Node series_node = Series(series_id, title, title_id, tag_line, feature_image, keywords, copyrights, dd_img, dd_summary, series_type, time_created, series_price);  // Creating a new series node
			if(series_node == null)
				throw new KahaniyaCustomException("Something went wrong, while creating series ");

			
			//create relationship with keywords
			keywords = keywords.toLowerCase();
			for(String keyword : keywords.split(","))
			{
				Node keyword_node = keyword_index.get(KEYWORD_NAME, keyword.toLowerCase()).getSingle();
				if(keyword_node == null)
				{
					keyword_node = Keyword(keyword);
					keyword_index.add(keyword_node, KEYWORD_NAME, keyword.toLowerCase());
				}
				createRelation(SERIES_KEYWORD, series_node, keyword_node);
				
			}
			//create relationships with Genres and Languages
			createRelation(SERIES_BELONGS_TO_GENRE, series_node, genreNode);
			createRelation(SERIES_BELONGS_TO_LANGUAGE, series_node, langNode);
			createRelation(SERIES_BELONGS_TO_GENRE_LANGUAGE, series_node, genre_lang_node);
			
			//create relationship with series_started_base_node
			if(series_type == 2) // for web series, it should be in started status, otherwise completed
				createRelation(SERIES_STARTED_REL_TO_BASE_NODE, series_node, seriesStartedBaseNode);
			else
				createRelation(SERIES_COMPLETED_REL_TO_BASE_NODE, series_node, seriesCompletedBaseNode);
			
			Node writingStyleNode = getWritingStyleNode(writing_style);
			createRelation(SERIES_BELONGS_TO_WRITING_STYLE, series_node, writingStyleNode);
			
			//Add series to contest
			if(contestId != null && contestId.length() != 0)
			{
				Index<Node> contestIdIndex = graphDb.index().forNodes(CONTEST_ID_INDEX);
				Node contestNode = contestIdIndex.get(CONTEST_ID, contestId).getSingle();
				if(contestNode != null)
					System.out.println("Series belongs to contest"+createRelation(SERIES_BELONGS_TO_CONTEST, series_node, contestNode));
				else
					throw new KahaniyaCustomException("Contest not found for the given contest id:"+contestId);
				series_node.getSingleRelationship(SERIES_BELONGS_TO_CONTEST, Direction.OUTGOING).setProperty(SERIES_CONTEST_STATUS, 0);
				
				if(Integer.parseInt(contestNode.getProperty(CONTEST_VISIBILTY).toString())==1){
					//create relationship with user
					createRelation(USER_STARTED_SERIES, userNode, series_node);
					
					//Indexing newly created series node
					seriesId_index.add(series_node, SERIES_ID, series_id);
					seriesTitleId_index.add(series_node, SERIES_TITLE_ID, title_id.toLowerCase());
					seriesType_index.add(series_node, SERIES_TYPE, series_type);

					search_index.add(series_node, SEARCH_SERIES, title +""+ title_id.toLowerCase());

				}else{
					System.out.println("Relation user started series closed"+createRelation(USER_STARTED_SERIES_ON_CLOSED_CONTEST, userNode, series_node));
					Index<Node> closedseriesIdIndex = graphDb.index().forNodes(CLOSED_SERIES_ID_INDEX);
					closedseriesIdIndex.add(series_node, SERIES_ID, series_id);					
				}	
			}else{
				//create relationship with user
				createRelation(USER_STARTED_SERIES, userNode, series_node);
				
				//Indexing newly created series node
				seriesId_index.add(series_node, SERIES_ID, series_id);
				seriesTitleId_index.add(series_node, SERIES_TITLE_ID, title_id.toLowerCase());
				seriesType_index.add(series_node, SERIES_TYPE, series_type);

				search_index.add(series_node, SEARCH_SERIES, title +""+ title_id.toLowerCase());

			}
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_series()");
			System.out.println("Something went wrong, while creating series from create_series  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_series()");
			System.out.println("Something went wrong, while creating series from create_series  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;

	}

	private String edit_series(String series_id, String user_id,
			String title, String title_id, String tag_line,
			String feature_image, String genre, String language,
			String keywords, String copyrights, String dd_img,
			String dd_summary, int series_type, int time_edited,
			String contestId, int series_price){

		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			if(title == null || title.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title");
			if(title_id == null || title_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title_id");
			if(genre == null || genre.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param genre");
			if(language == null || language.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param language");
			if(tag_line == null)
				tag_line = "";
			if(feature_image == null)
				feature_image = "";
			if(keywords == null)
				keywords = "";
			if(copyrights == null)
				copyrights = "";
			if(dd_img == null)
				dd_img = "";
			if(dd_summary == null)
				dd_summary = "";
			
			aquireWriteLock(tx);
			if(contestId == null || contestId.length() == 0)
			{
				Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
				Index<Node> seriesType_index = graphDb.index().forNodes(SERIES_TYPE_INDEX);
				
				Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
				Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
				Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
				Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);

				Index<Node> keyword_index = graphDb.index().forNodes(KEYWORD_INDEX);
				
				Node series_node = seriesId_index.get(SERIES_ID, series_id).getSingle();
				if(series_node == null)
					throw new KahaniyaCustomException("Series doesnot exists with given id : "+series_id);
				
				Node genreNode = genreName_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
				if(genreNode == null)
					throw new KahaniyaCustomException("Genre doesnot exists for the name : " + genre);
				
				Node langNode = langName_index.get(LANG_NAME, language.toLowerCase()).getSingle();
				if(langNode == null)
					throw new KahaniyaCustomException("Language doesnot exists for the name : " + language);
				
				Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, genre.toLowerCase()+" "+language.toLowerCase()).getSingle();
				if(genre_lang_node == null)
					throw new KahaniyaCustomException("Genre Language doesnot exists for the name : " + genre+" "+language);
				
				//remove existing relationships with Genres, Languages and keywords
				for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_GENRE))
					rel.delete();
				for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_LANGUAGE))
					rel.delete();
				for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_GENRE_LANGUAGE))
					rel.delete();
				for(Relationship rel : series_node.getRelationships(SERIES_KEYWORD))
					rel.delete();
				for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_CONTEST))
					rel.delete();

				// If series not tagged to contest, adding series to contest
				if(contestId != null && contestId.length() != 0)
				{
					Index<Node> contestIdIndex = graphDb.index().forNodes(CONTEST_ID_INDEX);
					Node contestNode = contestIdIndex.get(CONTEST_ID, contestId).getSingle();
					if(contestNode != null){
						if(!isRelationExistsBetween(SERIES_BELONGS_TO_CONTEST, series_node, contestNode)){
							Relationship rel = createRelation(SERIES_BELONGS_TO_CONTEST, series_node, contestNode);					
							rel.setProperty(SERIES_CONTEST_STATUS, 0);
						}
					}					
					else
						throw new KahaniyaCustomException("Contest not found for the given contest id:"+contestId);
				}
				
				//create relationships with Genres and Languages
				createRelation(SERIES_BELONGS_TO_GENRE, series_node, genreNode, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
				createRelation(SERIES_BELONGS_TO_LANGUAGE, series_node, langNode, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
				createRelation(SERIES_BELONGS_TO_GENRE_LANGUAGE, series_node, genre_lang_node, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
				
				//create relationship with keywords
				keywords = keywords.toLowerCase();
				for(String keyword : keywords.split(","))
				{
					Node keyword_node = keyword_index.get(KEYWORD_NAME, keyword.toLowerCase()).getSingle();
					if(keyword_node == null)
					{
						keyword_node = Keyword(keyword);
						keyword_index.add(keyword_node, KEYWORD_NAME, keyword.toLowerCase());
					}
					createRelation(SERIES_KEYWORD, series_node, keyword_node, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
					
				}
				
				series_node.setProperty(SERIES_TITLE, title);
				series_node.setProperty(SERIES_TITLE_ID, title_id);
				series_node.setProperty(SERIES_TAG_LINE, tag_line);
				series_node.setProperty(SERIES_FEAT_IMG, feature_image);
				series_node.setProperty(SERIES_KEYWORDS, keywords);
				series_node.setProperty(SERIES_COPYRIGHTS, copyrights);
				series_node.setProperty(SERIES_DD_IMG, dd_img);
				series_node.setProperty(SERIES_DD_SUMMARY, dd_summary);
				series_node.setProperty(SERIES_TYPE, series_type);
				series_node.setProperty(SERIES_PRICE, series_price);
				
				//update indexing for the edited series node
				seriesType_index.remove(series_node);
				seriesType_index.add(series_node, SERIES_TYPE, series_type);
				
				search_index.remove(series_node);
				search_index.add(series_node, SEARCH_SERIES, title +""+ title_id.toLowerCase());			
			}
			else
			{
				Index<Node> contestIdIndex = graphDb.index().forNodes(CONTEST_ID_INDEX);
				Node contestNode = contestIdIndex.get(CONTEST_ID, contestId).getSingle();
				
				if((Integer.parseInt(contestNode.getProperty(CONTEST_VISIBILTY).toString())==1)){
					
					Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
					Index<Node> seriesType_index = graphDb.index().forNodes(SERIES_TYPE_INDEX);
					
					Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
					Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
					Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
					Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);

					Index<Node> keyword_index = graphDb.index().forNodes(KEYWORD_INDEX);
					
					Node series_node = seriesId_index.get(SERIES_ID, series_id).getSingle();
					if(series_node == null)
						throw new KahaniyaCustomException("Series doesnot exists with given id : "+series_id);
					
					Node genreNode = genreName_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
					if(genreNode == null)
						throw new KahaniyaCustomException("Genre doesnot exists for the name : " + genre);
					
					Node langNode = langName_index.get(LANG_NAME, language.toLowerCase()).getSingle();
					if(langNode == null)
						throw new KahaniyaCustomException("Language doesnot exists for the name : " + language);
					
					Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, genre.toLowerCase()+" "+language.toLowerCase()).getSingle();
					if(genre_lang_node == null)
						throw new KahaniyaCustomException("Genre Language doesnot exists for the name : " + genre+" "+language);
					
					//remove existing relationships with Genres, Languages and keywords
					for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_GENRE))
						rel.delete();
					for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_LANGUAGE))
						rel.delete();
					for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_GENRE_LANGUAGE))
						rel.delete();
					for(Relationship rel : series_node.getRelationships(SERIES_KEYWORD))
						rel.delete();
					for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_CONTEST))
						rel.delete();

					// If series not tagged to contest, adding series to contest
					if(contestId != null && contestId.length() != 0)
					{
						if(contestNode != null){
							if(!isRelationExistsBetween(SERIES_BELONGS_TO_CONTEST, series_node, contestNode)){
								Relationship rel = createRelation(SERIES_BELONGS_TO_CONTEST, series_node, contestNode);					
								rel.setProperty(SERIES_CONTEST_STATUS, 0);
							}
						}					
						else
							throw new KahaniyaCustomException("Contest not found for the given contest id:"+contestId);
					}
					
					//create relationships with Genres and Languages
					createRelation(SERIES_BELONGS_TO_GENRE, series_node, genreNode, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
					createRelation(SERIES_BELONGS_TO_LANGUAGE, series_node, langNode, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
					createRelation(SERIES_BELONGS_TO_GENRE_LANGUAGE, series_node, genre_lang_node, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
					
					//create relationship with keywords
					keywords = keywords.toLowerCase();
					for(String keyword : keywords.split(","))
					{
						Node keyword_node = keyword_index.get(KEYWORD_NAME, keyword.toLowerCase()).getSingle();
						if(keyword_node == null)
						{
							keyword_node = Keyword(keyword);
							keyword_index.add(keyword_node, KEYWORD_NAME, keyword.toLowerCase());
						}
						createRelation(SERIES_KEYWORD, series_node, keyword_node, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
						
					}
					
					series_node.setProperty(SERIES_TITLE, title);
					series_node.setProperty(SERIES_TITLE_ID, title_id);
					series_node.setProperty(SERIES_TAG_LINE, tag_line);
					series_node.setProperty(SERIES_FEAT_IMG, feature_image);
					series_node.setProperty(SERIES_KEYWORDS, keywords);
					series_node.setProperty(SERIES_COPYRIGHTS, copyrights);
					series_node.setProperty(SERIES_DD_IMG, dd_img);
					series_node.setProperty(SERIES_DD_SUMMARY, dd_summary);
					series_node.setProperty(SERIES_TYPE, series_type);
					series_node.setProperty(SERIES_PRICE, series_price);
					
					//update indexing for the edited series node
					seriesType_index.remove(series_node);
					seriesType_index.add(series_node, SERIES_TYPE, series_type);
					
					search_index.remove(series_node);
					search_index.add(series_node, SEARCH_SERIES, title +""+ title_id.toLowerCase());

				}else{
					
					Index<Node> seriesId_index = graphDb.index().forNodes(CLOSED_SERIES_ID_INDEX);
					
					Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
					Index<Node> langName_index = graphDb.index().forNodes(LANG_NAME_INDEX);
					Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
					Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);

					Index<Node> keyword_index = graphDb.index().forNodes(KEYWORD_INDEX);
					
					Node series_node = seriesId_index.get(SERIES_ID, series_id).getSingle();
					if(series_node == null)
						throw new KahaniyaCustomException("Series doesnot exists with given id : "+series_id);
					
					Node genreNode = genreName_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
					if(genreNode == null)
						throw new KahaniyaCustomException("Genre doesnot exists for the name : " + genre);
					
					Node langNode = langName_index.get(LANG_NAME, language.toLowerCase()).getSingle();
					if(langNode == null)
						throw new KahaniyaCustomException("Language doesnot exists for the name : " + language);
					
					Node genre_lang_node = genre_lang_index.get(GENRE_LANG_NAME, genre.toLowerCase()+" "+language.toLowerCase()).getSingle();
					if(genre_lang_node == null)
						throw new KahaniyaCustomException("Genre Language doesnot exists for the name : " + genre+" "+language);
					
					//remove existing relationships with Genres, Languages and keywords
					for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_GENRE))
						rel.delete();
					for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_LANGUAGE))
						rel.delete();
					for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_GENRE_LANGUAGE))
						rel.delete();
					for(Relationship rel : series_node.getRelationships(SERIES_KEYWORD))
						rel.delete();
					for(Relationship rel : series_node.getRelationships(SERIES_BELONGS_TO_CONTEST))
						rel.delete();

					// If series not tagged to contest, adding series to contest
					if(contestId != null && contestId.length() != 0)
					{
						if(contestNode != null){
							if(!isRelationExistsBetween(SERIES_BELONGS_TO_CONTEST, series_node, contestNode)){
								Relationship rel = createRelation(SERIES_BELONGS_TO_CONTEST, series_node, contestNode);					
								rel.setProperty(SERIES_CONTEST_STATUS, 0);
							}
						}					
						else
							throw new KahaniyaCustomException("Contest not found for the given contest id:"+contestId);
					}
					
					//create relationships with Genres and Languages
					createRelation(SERIES_BELONGS_TO_GENRE, series_node, genreNode, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
					createRelation(SERIES_BELONGS_TO_LANGUAGE, series_node, langNode, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
					createRelation(SERIES_BELONGS_TO_GENRE_LANGUAGE, series_node, genre_lang_node, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
					
					//create relationship with keywords
					keywords = keywords.toLowerCase();
					for(String keyword : keywords.split(","))
					{
						Node keyword_node = keyword_index.get(KEYWORD_NAME, keyword.toLowerCase()).getSingle();
						if(keyword_node == null)
						{
							keyword_node = Keyword(keyword);
							keyword_index.add(keyword_node, KEYWORD_NAME, keyword.toLowerCase());
						}
						createRelation(SERIES_KEYWORD, series_node, keyword_node, Integer.parseInt(series_node.getProperty(TIME_CREATED).toString()));
						
					}
					
					series_node.setProperty(SERIES_TITLE, title);
					series_node.setProperty(SERIES_TITLE_ID, title_id);
					series_node.setProperty(SERIES_TAG_LINE, tag_line);
					series_node.setProperty(SERIES_FEAT_IMG, feature_image);
					series_node.setProperty(SERIES_KEYWORDS, keywords);
					series_node.setProperty(SERIES_COPYRIGHTS, copyrights);
					series_node.setProperty(SERIES_DD_IMG, dd_img);
					series_node.setProperty(SERIES_DD_SUMMARY, dd_summary);
					series_node.setProperty(SERIES_TYPE, series_type);
					series_node.setProperty(SERIES_PRICE, series_price);
					
					//update indexing for the edited series node
					seriesId_index.remove(series_node);
					seriesId_index.add(series_node, SERIES_ID, series_id);
				}
			}
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_series()");
			System.out.println("Something went wrong, while editing series from edit_series  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_series()");
			System.out.println("Something went wrong, while editing series from edit_series  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;

	}

	@Override
	public String create_or_edit_review(String series_id, String review_id,
			String data, String user_id, int time_created, int is_edit) throws TException {
		if(is_edit == 0)
			return create_review(series_id, review_id, data, user_id, time_created);
		else if(is_edit == 1)
			return edit_review(series_id, review_id, data, user_id, time_created);
		else return "false";		
	}
	
	public String create_review(String series_id, String review_id, String data, String user_id, int time_created)
	{
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			if(review_id == null || review_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param review_id");
			if(data == null || data.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param data");
			
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> reviewId_index = graphDb.index().forNodes(REVIEW_ID_INDEX);
			
			Node userNode = userId_index.get(USER_ID,user_id).getSingle();
			if(userNode == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			Node seriesNode = seriesId_index.get(SERIES_ID,series_id).getSingle();
			if(seriesNode == null)
				throw new KahaniyaCustomException("Series doesnot exists with given id : "+series_id);
			
			if(reviewId_index.get(REVIEW_ID,review_id).getSingle()!=null)
				throw new KahaniyaCustomException("Review already exists with given id : "+review_id);
			
			Node review_node = Review(review_id, data, time_created);  // Creating a new review node
			if(review_node == null)
				throw new KahaniyaCustomException("Something went wrong, while creating review ");

			//create relationship with user
			createRelation(USER_WRITTEN_A_REVIEW, userNode, review_node, time_created);
			
			//create relationships with Series
			createRelation(REVIEW_BELONGS_TO_SERIES, review_node, seriesNode, time_created);
			
			//Indexing newly created series node
			reviewId_index.add(review_node, REVIEW_ID, review_id);
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_review()");
			System.out.println("Something went wrong, while creating review from create_review  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_review()");
			System.out.println("Something went wrong, while creating review from create_review  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}

	public String edit_review(String series_id, String review_id, String data, String user_id, int time_edited)
	{

		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			if(review_id == null || review_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param review_id");
			if(data == null || data.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param data");
			

			aquireWriteLock(tx);
			
			Index<Node> reviewId_index = graphDb.index().forNodes(REVIEW_ID_INDEX);
			Node review_node = reviewId_index.get(REVIEW_ID, review_id).getSingle();
			if(review_node == null)
				throw new KahaniyaCustomException("Review doesnot exists with given id : "+review_id);
			
			review_node.setProperty(REVIEW_DATA, data);

			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_review()");
			System.out.println("Something went wrong, while editing review from edit_review  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_review()");
			System.out.println("Something went wrong, while editing review from edit_review  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}

	@Override
	public String create_or_edit_chapter(String chapter_id, String series_id,
			String series_type, String user_id, String title_id, String title,
			String feat_image, int time_created, int free_or_paid, int is_edit, String contestId, int w_count)
			throws TException {
		if(is_edit == 0)
			return create_chapter(chapter_id, series_id, series_type, user_id, title_id, title, feat_image, free_or_paid, time_created, contestId, w_count);
		else if(is_edit == 1)
			return edit_chapter(chapter_id, series_id, series_type, user_id, title_id, title, feat_image, free_or_paid, time_created, contestId, w_count);
		else return "false";		
	}
	
	private String edit_chapter(String chapter_id, String series_id,
			String series_type, String user_id, String title_id,
			String title, String feat_image, int free_or_paid,
			int time_created, String contestId, int w_count) {

		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");
			if(title_id == null || title_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title_id");
			if(title == null || title.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title");
			if(feat_image == null)
				feat_image = "";

			aquireWriteLock(tx);
			
			if(contestId == null || contestId.length() == 0){
				
				Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
				Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
				Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);			
				Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
				
				Node userNode = userId_index.get(USER_ID,user_id).getSingle();
				if(userNode == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				Node seriesNode = seriesId_index.get(SERIES_ID,series_id).getSingle();
				if(seriesNode == null)
					throw new KahaniyaCustomException("Series doesnot exists with given id : "+series_id);

				Node chapterNode = chapterId_index.get(CHAPTER_ID,chapter_id).getSingle();
				if(chapterNode == null)
					throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
				
				Node writingStyleNode = seriesNode.getSingleRelationship(SERIES_BELONGS_TO_WRITING_STYLE, Direction.OUTGOING).getEndNode();
				if(!isRelationExistsBetween(CHAPTER_BELONGS_TO_WRITING_STYLE, chapterNode, writingStyleNode))
				{
					Iterator<Relationship> styles = chapterNode.getRelationships(CHAPTER_BELONGS_TO_WRITING_STYLE).iterator();
					while(styles.hasNext())
						styles.next().delete();
					createRelation(CHAPTER_BELONGS_TO_WRITING_STYLE, chapterNode, writingStyleNode);
				}

				
//				for(Relationship rel : chapterNode.getRelationships(CHAPTER_BELONGS_TO_CONTEST))
//					rel.delete();
				// If chapter not tagged to contest, adding chapter to contest
				if(contestId != null && contestId.length() != 0)
				{
					Index<Node> contestIdIndex = graphDb.index().forNodes(CONTEST_ID_INDEX);
					Node contestNode = contestIdIndex.get(CONTEST_ID, contestId).getSingle();
					if(contestNode != null){
						if(!isRelationExistsBetween(CHAPTER_BELONGS_TO_CONTEST, chapterNode, contestNode)){
							Relationship rel = createRelation(CHAPTER_BELONGS_TO_CONTEST, chapterNode, contestNode);					
							rel.setProperty(CHAPTER_CONTEST_STATUS, 0);
						}
					}					
					else
						throw new KahaniyaCustomException("Contest not found for the given contest id:"+contestId);				
					
				}
				
				chapterNode.setProperty(CHAPTER_TITLE, title);
				chapterNode.setProperty(CHAPTER_TITLE_ID, title_id);
				chapterNode.setProperty(CHAPTER_FEAT_IMAGE, feat_image);
				chapterNode.setProperty(CHAPTER_FREE_OR_PAID, free_or_paid);
				chapterNode.setProperty(CHAPTER_WORDS_COUNT, w_count);
				
				search_index.remove(chapterNode);
				search_index.add(chapterNode, SEARCH_CHAPTER, title +""+ title_id.toLowerCase());
				
			}
			else{
				Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
				Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
				Index<Node> closedseriesId_index = graphDb.index().forNodes(CLOSED_SERIES_ID_INDEX);
//				Index<Node> chapterId_index = graphDb.index().forNodes(CLOSED_CHAPTER_ID_INDEX);			
				Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
				
				
				Node userNode = userId_index.get(USER_ID,user_id).getSingle();
				if(userNode == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				Node seriesNode = seriesId_index.get(SERIES_ID,series_id).getSingle();
				Node closedseriesNode = closedseriesId_index.get(SERIES_ID,series_id).getSingle();
				if(seriesNode == null || closedseriesNode == null)
					throw new KahaniyaCustomException("Series doesnot exists with given id : "+series_id);

				
				Index<Node> contestIdIndex = graphDb.index().forNodes(CONTEST_ID_INDEX);
				Node contestNode = contestIdIndex.get(CONTEST_ID, contestId).getSingle();
				
				if(Integer.parseInt(contestNode.getProperty(CONTEST_VISIBILTY).toString())==1){
					
					Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
					
					Node chapterNode = chapterId_index.get(CHAPTER_ID,chapter_id).getSingle();
					if(chapterNode == null)
						throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
					
					Node writingStyleNode = seriesNode.getSingleRelationship(SERIES_BELONGS_TO_WRITING_STYLE, Direction.OUTGOING).getEndNode();
					if(!isRelationExistsBetween(CHAPTER_BELONGS_TO_WRITING_STYLE, chapterNode, writingStyleNode))
					{
						Iterator<Relationship> styles = chapterNode.getRelationships(CHAPTER_BELONGS_TO_WRITING_STYLE).iterator();
						while(styles.hasNext())
							styles.next().delete();
						createRelation(CHAPTER_BELONGS_TO_WRITING_STYLE, chapterNode, writingStyleNode);
					}
					
					if(!isRelationExistsBetween(CHAPTER_BELONGS_TO_CONTEST, chapterNode, contestNode)){
						Relationship rel = createRelation(CHAPTER_BELONGS_TO_CONTEST, chapterNode, contestNode);					
						rel.setProperty(CHAPTER_CONTEST_STATUS, 0);
					}
					
					chapterNode.setProperty(CHAPTER_TITLE, title);
					chapterNode.setProperty(CHAPTER_TITLE_ID, title_id);
					chapterNode.setProperty(CHAPTER_FEAT_IMAGE, feat_image);
					chapterNode.setProperty(CHAPTER_FREE_OR_PAID, free_or_paid);
					chapterNode.setProperty(CHAPTER_WORDS_COUNT, w_count);
					
					search_index.remove(chapterNode);
					search_index.add(chapterNode, SEARCH_CHAPTER, title +""+ title_id.toLowerCase());

				}else{
					Index<Node> chapterId_index = graphDb.index().forNodes(CLOSED_CHAPTER_ID_INDEX);
					Node chapterNode = chapterId_index.get(CHAPTER_ID,chapter_id).getSingle();
					if(chapterNode == null)
						throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
					
					Node writingStyleNode = closedseriesNode.getSingleRelationship(SERIES_BELONGS_TO_WRITING_STYLE, Direction.OUTGOING).getEndNode();
					if(!isRelationExistsBetween(CHAPTER_BELONGS_TO_WRITING_STYLE, chapterNode, writingStyleNode))
					{
						Iterator<Relationship> styles = chapterNode.getRelationships(CHAPTER_BELONGS_TO_WRITING_STYLE).iterator();
						while(styles.hasNext())
							styles.next().delete();
						createRelation(CHAPTER_BELONGS_TO_WRITING_STYLE, chapterNode, writingStyleNode);
					}
					
					if(!isRelationExistsBetween(CHAPTER_BELONGS_TO_CONTEST, chapterNode, contestNode)){
						Relationship rel = createRelation(CHAPTER_BELONGS_TO_CONTEST, chapterNode, contestNode);					
						rel.setProperty(CHAPTER_CONTEST_STATUS, 0);
					}
					
					
					chapterNode.setProperty(CHAPTER_TITLE, title);
					chapterNode.setProperty(CHAPTER_TITLE_ID, title_id);
					chapterNode.setProperty(CHAPTER_FEAT_IMAGE, feat_image);
					chapterNode.setProperty(CHAPTER_FREE_OR_PAID, free_or_paid);
					chapterNode.setProperty(CHAPTER_WORDS_COUNT, w_count);					
					
				}
			}
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_chapter()");
			System.out.println("Something went wrong, while editing chapter from edit_chapter  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_chapter()");
			System.out.println("Something went wrong, while editing chapter from edit_chapter  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}

	private String create_chapter(String chapter_id, String series_id,
			String series_type, String user_id, String title_id,
			String title, String feat_image, int free_or_paid, 
			int time_created, String contestId, int w_count) {
		
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");
			if(title_id == null || title_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title_id");
			if(title == null || title.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title");
			if(feat_image == null)
				feat_image = "";


			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> closedseriesId_index = graphDb.index().forNodes(CLOSED_SERIES_ID_INDEX);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> chapterTitleId_index = graphDb.index().forNodes(CHAPTER_TITLE_ID_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Index<Node> series_status_base_node_index = graphDb.index().forNodes(LOCK_INDEX);
			Node seriesOngoingBaseNode = series_status_base_node_index.get(SERIES_STATUS, SERIES_ONGOING_BASE_NODE).getSingle();
			if(seriesOngoingBaseNode == null)
				throw new KahaniyaCustomException("Unable to find SERIES_ONGOING_BASE_NODE");
			

			
			Node userNode = userId_index.get(USER_ID,user_id).getSingle();
			if(userNode == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			System.out.println("Regular -- "+seriesId_index);
			System.out.println("Closed  -- "+closedseriesId_index);
			
			Node seriesNode = seriesId_index.get(SERIES_ID, series_id).getSingle();
			Node closedseriesNode = closedseriesId_index.get(SERIES_ID, series_id).getSingle();
			
			System.out.println("Regular -- "+seriesNode);
			System.out.println("Closed  -- "+closedseriesNode);

//			if(seriesNode == null || closedseriesNode == null)
//				throw new KahaniyaCustomException("Series doesnot exists with given id : "+series_id);

			if(chapterId_index.get(CHAPTER_ID,chapter_id).getSingle()!=null)
				throw new KahaniyaCustomException("Chapter already exists with given id : "+chapter_id);
			
//			if(chapterTitleId_index.get(CHAPTER_TITLE_ID,title_id.toLowerCase()).getSingle()!=null)
//				throw new KahaniyaCustomException("Chapter already exists with given title id : "+title_id);
			
			Node chapter_node = Chapter(chapter_id, title_id, title, feat_image, free_or_paid, time_created, w_count);  // Creating a new chapter node
			if(chapter_node == null)
				throw new KahaniyaCustomException("Something went wrong, while creating chapter ");
			
			if(contestId != null && contestId.length() != 0)
			{
				Index<Node> contestIdIndex = graphDb.index().forNodes(CONTEST_ID_INDEX);
				Node contestNode = contestIdIndex.get(CONTEST_ID, contestId).getSingle();
				if(contestNode != null)
					System.out.println("Relation Created --- "+createRelation(CHAPTER_BELONGS_TO_CONTEST, chapter_node, contestNode));
				else
					throw new KahaniyaCustomException("Contest not found for the given contest id:"+contestId);
				chapter_node.getSingleRelationship(CHAPTER_BELONGS_TO_CONTEST, Direction.OUTGOING).setProperty(CHAPTER_CONTEST_STATUS, 0);
				
				if(Integer.parseInt(contestNode.getProperty(CONTEST_VISIBILTY).toString()) == 1)
				{
					//create relationships with Series
					createRelation(CHAPTER_BELONGS_TO_SERIES, chapter_node, seriesNode, time_created);

					Node writingStyleNode = seriesNode.getSingleRelationship(SERIES_BELONGS_TO_WRITING_STYLE, Direction.OUTGOING).getEndNode();
					createRelation(CHAPTER_BELONGS_TO_WRITING_STYLE, chapter_node, writingStyleNode);
			

					//create relationship with user
					createRelation(USER_WRITTEN_A_CHAPTER, userNode, chapter_node, time_created);
					
					//Indexing newly created series node
					chapterId_index.add(chapter_node, CHAPTER_ID, chapter_id);
					chapterTitleId_index.add(chapter_node, CHAPTER_TITLE_ID, title_id.toLowerCase());
					search_index.add(chapter_node, SEARCH_CHAPTER, title +""+ title_id.toLowerCase());
					
					//update relation with base series if series is in started status
					//no need to update if series is ongoing / completed
					if(seriesNode.hasRelationship(SERIES_STARTED_REL_TO_BASE_NODE))
					{
						seriesNode.getSingleRelationship(SERIES_STARTED_REL_TO_BASE_NODE, Direction.OUTGOING).delete();
						createRelation(SERIES_ONGOING_REL_TO_BASE_NODE, seriesNode, seriesOngoingBaseNode);
					}
				}
				else{
					//create relationships with Series
					createRelation(CHAPTER_BELONGS_TO_SERIES, chapter_node, closedseriesNode, time_created);

					Node writingStyleNode = closedseriesNode.getSingleRelationship(SERIES_BELONGS_TO_WRITING_STYLE, Direction.OUTGOING).getEndNode();
					createRelation(CHAPTER_BELONGS_TO_WRITING_STYLE, chapter_node, writingStyleNode);
			

					//create relationship with user
					createRelation(USER_WRITTEN_A_CHAPTER_TO_CLOSED_CONTEST, userNode, chapter_node, time_created);
					Index<Node> closedchapterId_index = graphDb.index().forNodes(CLOSED_CHAPTER_ID_INDEX);
					closedchapterId_index.add(chapter_node, CHAPTER_ID, chapter_id);
					
					//update relation with base series if series is in started status
					//no need to update if series is ongoing / completed
					if(closedseriesNode.hasRelationship(SERIES_STARTED_REL_TO_BASE_NODE))
					{
						closedseriesNode.getSingleRelationship(SERIES_STARTED_REL_TO_BASE_NODE, Direction.OUTGOING).delete();
						createRelation(SERIES_ONGOING_REL_TO_BASE_NODE, closedseriesNode, seriesOngoingBaseNode);
					}

				}

			}
			else{
				//create relationships with Series
				createRelation(CHAPTER_BELONGS_TO_SERIES, chapter_node, seriesNode, time_created);

				Node writingStyleNode = seriesNode.getSingleRelationship(SERIES_BELONGS_TO_WRITING_STYLE, Direction.OUTGOING).getEndNode();
				createRelation(CHAPTER_BELONGS_TO_WRITING_STYLE, chapter_node, writingStyleNode);

				//create relationship with user
				createRelation(USER_WRITTEN_A_CHAPTER, userNode, chapter_node, time_created);
				
				//Indexing newly created series node
				chapterId_index.add(chapter_node, CHAPTER_ID, chapter_id);
				chapterTitleId_index.add(chapter_node, CHAPTER_TITLE_ID, title_id.toLowerCase());
				search_index.add(chapter_node, SEARCH_CHAPTER, title +""+ title_id.toLowerCase());
				
				//update relation with base series if series is in started status
				//no need to update if series is ongoing / completed
				if(seriesNode.hasRelationship(SERIES_STARTED_REL_TO_BASE_NODE))
				{
					seriesNode.getSingleRelationship(SERIES_STARTED_REL_TO_BASE_NODE, Direction.OUTGOING).delete();
					createRelation(SERIES_ONGOING_REL_TO_BASE_NODE, seriesNode, seriesOngoingBaseNode);
				}


			}
			
			
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_chapter()");
			System.out.println("Something went wrong, while creating chapter from create_chapter  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_chapter()");
			System.out.println("Something went wrong, while creating chapter from create_chapter  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	// Update chapter Words count value
	@Override
	public String update_chapter_words_count(String chapter_id, int w_count) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");
			if(w_count <= 0)
				throw new KahaniyaCustomException("Valid words count is greater than 0, receieved "+w_count+" for the param status ");

			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
						
			Node chapter_node = chapterId_index.get(CHAPTER_ID, chapter_id).getSingle();
			if(chapter_node == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
			
			chapter_node.setProperty(CHAPTER_WORDS_COUNT, w_count);
								
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ update_chapter_contest_status()");
			System.out.println("Something went wrong, while updating chapter_words_count  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ update_chapter_contest_status()");
			System.out.println("Something went wrong, while updating chapter_words_count  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	// Edit / Update user_bio
	@Override
	public String edit_user_bio(String user_id, String bio) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
						
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+user_id);
			
			user_node.setProperty(BIO, bio);
								
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_bio()");
			System.out.println("Something went wrong, while updating edit_user_bio  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_user_bio()");
			System.out.println("Something went wrong, while updating edit_user_bio  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}	

	@Override
	public String update_chapter_contest_status(String chapter_id, String contest_id, int status) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(contest_id == null || contest_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param contest_id");
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");
			if(status != 0 && status != 1)
				throw new KahaniyaCustomException("Valid status is either 0 or 1, receieved "+status+" for the param status ");

			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			
			Node chapter_node = chapterId_index.get(CHAPTER_ID, chapter_id).getSingle();
			if(chapter_node == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
			Node contest_node = contestId_index.get(CONTEST_ID, contest_id).getSingle();
			if(contest_node == null)
				throw new KahaniyaCustomException("Contest doesnot exists with given id : "+contest_id);
			
			if(isRelationExistsBetween(CHAPTER_BELONGS_TO_CONTEST, chapter_node, contest_node))
			{
				chapter_node.getSingleRelationship(CHAPTER_BELONGS_TO_CONTEST, Direction.OUTGOING).setProperty(CHAPTER_CONTEST_STATUS, status);
			}
			else
				throw new KahaniyaCustomException("Given chapter does not belongs to the given contest");
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ update_chapter_contest_status()");
			System.out.println("Something went wrong, while updating contest status of a chapter from update_chapter_contest_status  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ update_chapter_contest_status()");
			System.out.println("Something went wrong, while updating contest status of a chapter from update_chapter_contest_status  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String update_series_contest_status(String series_id, String contest_id, int status) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(contest_id == null || contest_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param contest_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");
			if(status != 0 && status != 1)
				throw new KahaniyaCustomException("Valid status is either 0 or 1, receieved "+status+" for the param status ");

			aquireWriteLock(tx);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			
			Node series_node = seriesId_index.get(SERIES_ID, series_id).getSingle();
			if(series_node == null)
				throw new KahaniyaCustomException("series doesnot exists with given id : "+series_id);
			Node contest_node = contestId_index.get(CONTEST_ID, contest_id).getSingle();
			if(contest_node == null)
				throw new KahaniyaCustomException("Contest doesnot exists with given id : "+contest_id);
			
			if(isRelationExistsBetween(SERIES_BELONGS_TO_CONTEST, series_node, contest_node))
			{
				series_node.getSingleRelationship(SERIES_BELONGS_TO_CONTEST, Direction.OUTGOING).setProperty(SERIES_CONTEST_STATUS, status);
			}
			else
				throw new KahaniyaCustomException("Given chapter does not belongs to the given contest");
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ update_series_contest_status()");
			System.out.println("Something went wrong, while updating contest status of a chapter from update_series_contest_status  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ update_series_contest_status()");
			System.out.println("Something went wrong, while updating contest status of a chapter from update_series_contest_status  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String update_nanostory_contest_status(String nanostory_id, String contest_id, int status) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(contest_id == null || contest_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param contest_id");
			if(nanostory_id == null || nanostory_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param nanostory_id");
			if(status != 0 && status != 1)
				throw new KahaniyaCustomException("Valid status is either 0 or 1, receieved "+status+" for the param status ");

			aquireWriteLock(tx);
			Index<Node> nanostoryId_index = graphDb.index().forNodes(NANOSTORY_ID_INDEX);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			
			Node nanostory_node = nanostoryId_index.get(NANOSTORY_ID, nanostory_id).getSingle();
			if(nanostory_node == null)
				throw new KahaniyaCustomException("nanostory doesnot exists with given id : "+nanostory_id);
			Node contest_node = contestId_index.get(CONTEST_ID, contest_id).getSingle();
			if(contest_node == null)
				throw new KahaniyaCustomException("Contest doesnot exists with given id : "+contest_id);
			
			if(isRelationExistsBetween(NANOSTORY_BELONGS_TO_CONTEST, nanostory_node, contest_node))
			{
				nanostory_node.getSingleRelationship(NANOSTORY_BELONGS_TO_CONTEST, Direction.OUTGOING).setProperty(NANOSTORY_CONTEST_STATUS, status);
			}
			else
				throw new KahaniyaCustomException("Given chapter does not belongs to the given contest");
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ update_nanostory_contest_status()");
			System.out.println("Something went wrong, while updating contest status of a chapter from update_nanostory_contest_status  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ update_nanostory_contest_status()");
			System.out.println("Something went wrong, while updating contest status of a chapter from update_nanostory_contest_status  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}


	@Override
	public String record_contest_judge_rating_on_chapter(String contest_id, String chapter_id, String user_id) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(contest_id == null || contest_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param contest_id");
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			
			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node chapter_node = chapterId_index.get(CHAPTER_ID, chapter_id).getSingle();
			if(chapter_node == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
			Node contest_node = contestId_index.get(CONTEST_ID, contest_id).getSingle();
			if(contest_node == null)
				throw new KahaniyaCustomException("Contest doesnot exists with given id : "+contest_id);
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(isRelationExistsBetween(CHAPTER_BELONGS_TO_CONTEST, chapter_node, contest_node))
			{
				Relationship rel = chapter_node.getSingleRelationship(CHAPTER_BELONGS_TO_CONTEST, Direction.OUTGOING);
				if(!rel.hasProperty(CHAPTER_CONTEST_RATED_BY_JUDGES))
					rel.setProperty(CHAPTER_CONTEST_RATED_BY_JUDGES,user_id);
				else
				{
					String judgeNames = rel.getProperty(CHAPTER_CONTEST_RATED_BY_JUDGES).toString();
					if(!judgeNames.contains(user_id))
						rel.setProperty(CHAPTER_CONTEST_RATED_BY_JUDGES,judgeNames+user_id);
				}
			}
			else
				throw new KahaniyaCustomException("Given chapter does not belongs to the given contest");
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_contest_judge_rating_on_chapter()");
			System.out.println("Something went wrong, while updating contest judge rating status of a chapter from update_chapter_contest_status  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_contest_judge_rating_on_chapter()");
			System.out.println("Something went wrong, while updating contest judge rating status of a chapter from update_chapter_contest_status  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String record_contest_judge_rating_on_series(String contest_id, String series_id, String user_id) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(contest_id == null || contest_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param contest_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			
			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node series_node = chapterId_index.get(SERIES_ID, series_id).getSingle();
			if(series_node == null)
				throw new KahaniyaCustomException("series doesnot exists with given id : "+series_id);
			Node contest_node = contestId_index.get(CONTEST_ID, contest_id).getSingle();
			if(contest_node == null)
				throw new KahaniyaCustomException("Contest doesnot exists with given id : "+contest_id);
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(isRelationExistsBetween(SERIES_BELONGS_TO_CONTEST, series_node, contest_node))
			{
				Relationship rel = series_node.getSingleRelationship(SERIES_BELONGS_TO_CONTEST, Direction.OUTGOING);
				if(!rel.hasProperty(SERIES_CONTEST_RATED_BY_JUDGES))
					rel.setProperty(SERIES_CONTEST_RATED_BY_JUDGES,user_id);
				else
				{
					String judgeNames = rel.getProperty(SERIES_CONTEST_RATED_BY_JUDGES).toString();
					if(!judgeNames.contains(user_id))
						rel.setProperty(SERIES_CONTEST_RATED_BY_JUDGES,judgeNames+user_id);
				}
			}
			else
				throw new KahaniyaCustomException("Given chapter does not belongs to the given contest");
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_contest_judge_rating_on_series()");
			System.out.println("Something went wrong, while updating contest judge rating status of a chapter from update_series_contest_status  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_contest_judge_rating_on_series()");
			System.out.println("Something went wrong, while updating contest judge rating status of a chapter from update_series_contest_status  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String record_contest_judge_rating_on_nanostory(String contest_id, String nanostory_id, String user_id) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(contest_id == null || contest_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param contest_id");
			if(nanostory_id == null || nanostory_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param nanostory_id");
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			
			aquireWriteLock(tx);
			Index<Node> nanostoryId_index = graphDb.index().forNodes(NANOSTORY_ID_INDEX);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node nanostory_node = nanostoryId_index.get(NANOSTORY_ID, nanostory_id).getSingle();
			if(nanostory_node == null)
				throw new KahaniyaCustomException("nanostory doesnot exists with given id : "+nanostory_id);
			Node contest_node = contestId_index.get(CONTEST_ID, contest_id).getSingle();
			if(contest_node == null)
				throw new KahaniyaCustomException("Contest doesnot exists with given id : "+contest_id);
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(isRelationExistsBetween(NANOSTORY_BELONGS_TO_CONTEST, nanostory_node, contest_node))
			{
				Relationship rel = nanostory_node.getSingleRelationship(NANOSTORY_BELONGS_TO_CONTEST, Direction.OUTGOING);
				if(!rel.hasProperty(NANOSTORY_CONTEST_RATED_BY_JUDGES))
					rel.setProperty(NANOSTORY_CONTEST_RATED_BY_JUDGES,user_id);
				else
				{
					String judgeNames = rel.getProperty(NANOSTORY_CONTEST_RATED_BY_JUDGES).toString();
					if(!judgeNames.contains(user_id))
						rel.setProperty(NANOSTORY_CONTEST_RATED_BY_JUDGES,judgeNames+user_id);
				}
			}
			else
				throw new KahaniyaCustomException("Given chapter does not belongs to the given contest");
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_contest_judge_rating_on_nanostory()");
			System.out.println("Something went wrong, while updating contest judge rating status of a chapter from update_nanostory_contest_status  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_contest_judge_rating_on_series()");
			System.out.println("Something went wrong, while updating contest judge rating status of a chapter from update_series_contest_status  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}


	
	@Override
	public String get_pins(String post_type, String lang)throws TException {

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			if(lang == null || lang.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param lang");
			
			aquireWriteLock(tx);
			
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
			LinkedList<Node> resultList = new LinkedList<Node>();
			
			if(post_type.equalsIgnoreCase("A")){
				Iterator<Relationship> pinnedAuthors = langNode.getRelationships(USER_PINNED_TO_LANGUAGE).iterator();
				while(pinnedAuthors.hasNext())
					resultList.add(pinnedAuthors.next().getStartNode());
				
				for(Node user : resultList)
					jsonArray.put(getJSONForUser(user, null));
			}			
			else if(post_type.equalsIgnoreCase("C")){
				Iterator<Relationship> pinnedChapters = langNode.getRelationships(CHAPTER_PINNED_TO_LANGUAGE, Direction.INCOMING).iterator();
				while(pinnedChapters.hasNext())
					resultList.add(pinnedChapters.next().getStartNode());
				
				for(Node chapter : resultList)
					jsonArray.put(getJSONForChapter(chapter, null));
			}
			else if(post_type.equalsIgnoreCase("S")){
				Iterator<Relationship> pinnedSeries = langNode.getRelationships(SERIES_PINNED_TO_LANGUAGE, Direction.INCOMING).iterator();
				while(pinnedSeries.hasNext())
					resultList.add(pinnedSeries.next().getStartNode());
				
				for(Node series : resultList)
					jsonArray.put(getJSONForSeries(series, null));
			}
			else if(post_type.equalsIgnoreCase("AN")){
				Iterator<Relationship> pinnedAnthology = langNode.getRelationships(ANTHOLOGY_PINNED_TO_LANGUAGE, Direction.INCOMING).iterator();
				while(pinnedAnthology.hasNext())
					resultList.add(pinnedAnthology.next().getStartNode());
				
				for(Node anthology : resultList)
					jsonArray.put(getJSONForAnthologies(anthology, null));
			}
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ pin_a_post()");
			System.out.println("Something went wrong, while adding a pin :"+ex.getMessage());
//				ex.printStackTrace();
			return jsonArray.toString();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ pin_a_post()");
			System.out.println("Something went wrong, while adding a pin  :"+ex.getMessage());
			ex.printStackTrace();
			return jsonArray.toString();
		}
		return jsonArray.toString();
		
	}
	
	@Override
	public String pin_a_post(String post_type, String lang, String post_id, int time, int is_edit)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(lang == null || lang.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param lang");
			if(post_id == null || post_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param post_id");

			aquireWriteLock(tx);
			if(post_type.equalsIgnoreCase("A")){
				Index<Node> user_index = graphDb.index().forNodes(USER_ID_INDEX);
				Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
				
				Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
				Node user_node = user_index.get(USER_ID, post_id).getSingle();
				
				if(is_edit == 1)
					createRelation(USER_PINNED_TO_LANGUAGE, user_node, langNode, time);
				else
					deleteRelation(USER_PINNED_TO_LANGUAGE, user_node, langNode);				
				
			}			
			else if(post_type.equalsIgnoreCase("C")){
				Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);				
				Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
				
				Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
				Node chapter_node = chapterId_index.get(CHAPTER_ID, post_id).getSingle();
				
				if(is_edit == 1)
					createRelation(CHAPTER_PINNED_TO_LANGUAGE, chapter_node, langNode, time);
				else
					deleteRelation(CHAPTER_PINNED_TO_LANGUAGE, chapter_node, langNode);
				
			}
			else if(post_type.equalsIgnoreCase("S")){
				Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
				Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
				
				Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
				Node series_node = seriesId_index.get(SERIES_ID, post_id).getSingle();
				
				if(is_edit == 1)
					createRelation(SERIES_PINNED_TO_LANGUAGE, series_node, langNode, time);
				else
					deleteRelation(SERIES_PINNED_TO_LANGUAGE, series_node, langNode);
					
			}
			else if(post_type.equalsIgnoreCase("AN")){
				Index<Node> anthologyId_index = graphDb.index().forNodes(ANTHOLOGY_ID_INDEX);
				Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
				
				Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
				Node anthology_node = anthologyId_index.get(ANTHOLOGY_ID, post_id).getSingle();
				
				if(is_edit == 1)
					createRelation(ANTHOLOGY_PINNED_TO_LANGUAGE, anthology_node, langNode, time);
				else
					deleteRelation(ANTHOLOGY_PINNED_TO_LANGUAGE, anthology_node, langNode);
					
			}
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ pin_a_post()");
			System.out.println("Something went wrong, while adding a pin :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ pin_a_post()");
			System.out.println("Something went wrong, while adding a pin  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}


	
	@Override
	public String subscribe_series(String series_id, String user_id, int time)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");

			aquireWriteLock(tx);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node series_node = seriesId_index.get(SERIES_ID, series_id).getSingle();
			if(series_node == null)
				throw new KahaniyaCustomException("Series doesnot exists with given id : "+series_id);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(isRelationExistsBetween(USER_SUBSCRIBED_TO_SERIES, user_node, series_node))
				deleteRelation(USER_SUBSCRIBED_TO_SERIES, user_node, series_node);
			else
				createRelation(USER_SUBSCRIBED_TO_SERIES, user_node, series_node, time);
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ subscribe_series()");
			System.out.println("Something went wrong, while subscribing series from subscribe_series  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ subscribe_series()");
			System.out.println("Something went wrong, while subscribing series from subscribe_series  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String skip_subscribe_series(String series_id, String user_id, int time)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");

			aquireWriteLock(tx);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node series_node = seriesId_index.get(SERIES_ID, series_id).getSingle();
			if(series_node == null)
				throw new KahaniyaCustomException("Series doesnot exists with given id : "+series_id);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(isRelationExistsBetween(USER_SKIP_SUBSCRIBED_TO_SERIES, user_node, series_node))
				deleteRelation(USER_SKIP_SUBSCRIBED_TO_SERIES, user_node, series_node);
			else
				createRelation(USER_SKIP_SUBSCRIBED_TO_SERIES, user_node, series_node, time);
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ subscribe_series()");
			System.out.println("Something went wrong, while skip subscribing series from series subscribe suggestions :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ subscribe_series()");
			System.out.println("Something went wrong, while skip subscribing series from series subscribe suggestions  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}

	
	@Override
	public String subscribe_contest(String contest_id, String user_id, int time) 
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(contest_id == null || contest_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");

			aquireWriteLock(tx);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node contest_node = contestId_index.get(CONTEST_ID, contest_id).getSingle();
			if(contest_node == null)
				throw new KahaniyaCustomException("Contest doesnot exists with given id : "+contest_id);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(isRelationExistsBetween(USER_SUBSCRIBED_TO_CONTEST, user_node, contest_node))
				deleteRelation(USER_SUBSCRIBED_TO_CONTEST, user_node, contest_node);
			else
				createRelation(USER_SUBSCRIBED_TO_CONTEST, user_node, contest_node, time);
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ subscribe_contest()");
			System.out.println("Something went wrong, while subscribing contest from subscribe_contest  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ subscribe_contest()");
			System.out.println("Something went wrong, while subscribing series from subscribe_contest  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String like_or_dislike_nanostory(String nanostory_id, String user_id, int time, int state) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(nanostory_id == null || nanostory_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param nanostory_id");
			
			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(NANOSTORY_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node nanostory_node = chapterId_index.get(NANOSTORY_ID, nanostory_id).getSingle();
			if(nanostory_node == null)
				throw new KahaniyaCustomException("Nanostory doesnot exists with given id : "+nanostory_id);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(state == 0)
			{
				if(isRelationExistsBetween(USER_LIKED_NANOSTORY, user_node, nanostory_node))
					deleteRelation(USER_LIKED_NANOSTORY, user_node, nanostory_node);
				else
					deleteRelation(USER_DISLIKED_NANOSTORY, user_node, nanostory_node);
			}
			else if (state ==1){
				if(isRelationExistsBetween(USER_DISLIKED_NANOSTORY, user_node, nanostory_node))
					deleteRelation(USER_DISLIKED_NANOSTORY, user_node, nanostory_node);
				
				createRelation(USER_LIKED_NANOSTORY, user_node, nanostory_node, time);
			}	
			else if (state ==2){
				if(isRelationExistsBetween(USER_LIKED_NANOSTORY, user_node, nanostory_node))
					deleteRelation(USER_LIKED_NANOSTORY, user_node, nanostory_node);
				
				createRelation(USER_DISLIKED_NANOSTORY, user_node, nanostory_node, time);
			}
				
			else return "false";
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ like_or_dislike_nanostory()");
			System.out.println("Something went wrong, while like or dislike a nanostory  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ like_or_dislike_nanostory()");
			System.out.println("Something went wrong, while like or dislike a nanostory  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}


	@Override
	public String favourite_chapter(String chapter_id, String series_id,
			String user_id, int time) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");

			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node chapter_node = chapterId_index.get(CHAPTER_ID, chapter_id).getSingle();
			if(chapter_node == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(isRelationExistsBetween(USER_FAV_CHAPTER, user_node, chapter_node))
				deleteRelation(USER_FAV_CHAPTER, user_node, chapter_node);
			else
				createRelation(USER_FAV_CHAPTER, user_node, chapter_node, time);
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ favourite_chapter()");
			System.out.println("Something went wrong, while favourite a chapter from favourite_chapter  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ favourite_chapter()");
			System.out.println("Something went wrong, while favourite a chapter from favourite_chapter  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String bookmark_chapter(String chapter_id, String series_id,
			String user_id, int time) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");

			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node chapter_node = chapterId_index.get(CHAPTER_ID, chapter_id).getSingle();
			if(chapter_node == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(isRelationExistsBetween(USER_BOOKMARK_CHAPTER, user_node, chapter_node))
				deleteRelation(USER_BOOKMARK_CHAPTER, user_node, chapter_node);
			else
				createRelation(USER_BOOKMARK_CHAPTER, user_node, chapter_node, time);
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ bookmark_chapter()");
			System.out.println("Something went wrong, while bookmark a chapter from bookmark_chapter  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ bookmark_chapter()");
			System.out.println("Something went wrong, while bookmark a chapter from bookmark_chapter  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String rate_chapter(String chapter_id, String series_id, int rating,
			String user_id, int time) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");

			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node chapter_node = chapterId_index.get(CHAPTER_ID, chapter_id).getSingle();
			if(chapter_node == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(isRelationExistsBetween(USER_RATED_A_CHAPTER, user_node, chapter_node))
				deleteRelation(USER_RATED_A_CHAPTER, user_node, chapter_node);
			
			createRelation(USER_RATED_A_CHAPTER, user_node, chapter_node, time).setProperty(CHAPTER_RATING, rating);
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ rate_chapter()");
			System.out.println("Something went wrong, while rating a chapter from rate_chapter  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ rate_chapter()");
			System.out.println("Something went wrong, while rating a chapter from rate_chapter  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}

	@Override
	public String purchase_chapter(String chapter_id, String series_id, int price,
			String user_id, int time) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");

			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Index<Relationship> purchasedChapterRelIndex = graphDb.index().forRelationships(USER_PURCHASED_CHAPTER_REL_INDEX);
			
			Node chapter_node = chapterId_index.get(CHAPTER_ID, chapter_id).getSingle();
			if(chapter_node == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			
			if(isRelationExistsBetween(USER_PURCHASED_A_CHAPTER, user_node, chapter_node))
				throw new KahaniyaCustomException("User :"+user_id+" already purchased this chapter :"+chapter_id);
			else
			{
				Relationship rel = createRelation(USER_PURCHASED_A_CHAPTER, user_node, chapter_node, time);
				rel.setProperty(CHAPTER_PURCHASED_PRICE, price);
				purchasedChapterRelIndex.add(rel, USER_PURCHASED_CHAPTER_REL_INDEX, user_id+"_purchased_"+chapter_id);
			}			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ purchase_chapter()");
			System.out.println("Something went wrong, while purchasing a chapter from purchase_chapter  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ purchase_chapter()");
			System.out.println("Something went wrong, while purchasing a chapter from purchase_chapter  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String purchase_item(String item_id, String item_type, int price,
			String user_id, int time) throws TException {
		String res = null;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(item_type == null || item_type.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param item_type");
			if(item_id == null || item_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param item_id");

			aquireWriteLock(tx);
			
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			if(item_type.equalsIgnoreCase("C"))
			{
				Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
//				Index<Relationship> purchasedChapterRelIndex = graphDb.index().forRelationships(USER_PURCHASED_CHAPTER_REL_INDEX);
				Node chapter_node = chapterId_index.get(CHAPTER_ID, item_id).getSingle();
				if(chapter_node == null)
					throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+item_id);
				
				Node user_node = userId_index.get(USER_ID, user_id).getSingle();
				if(user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				
				if(isRelationExistsBetween(USER_PURCHASED_A_CHAPTER, user_node, chapter_node))
					throw new KahaniyaCustomException("User :"+user_id+" already purchased this chapter :"+item_id);
				else
				{
					Relationship rel = createRelation(USER_PURCHASED_A_CHAPTER, user_node, chapter_node, time);
					rel.setProperty(CHAPTER_PURCHASED_PRICE, price);
					Node series = chapter_node.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
					if(Integer.parseInt(series.getProperty(SERIES_TYPE).toString()) == 1){
						graphDb.index().forNodes(PURCHASED_CHAPTER_ID_INDEX).remove(chapter_node, CHAPTER_ID, item_id);
						graphDb.index().forNodes(PURCHASED_CHAPTER_ID_INDEX).add(chapter_node, CHAPTER_ID, item_id);
					}else{
						graphDb.index().forNodes(PURCHASED_SERIES_ID_INDEX).remove(series, SERIES_ID, item_id);
						graphDb.index().forNodes(PURCHASED_SERIES_ID_INDEX).add(series, SERIES_ID, item_id);
					}
					
//					purchasedChapterRelIndex.add(rel, USER_PURCHASED_CHAPTER_REL_INDEX, user_id+"_purchased_"+item_id);
					
//					createRelation(USER_PURCHASED_A_CHAPTER, user_node, chapter_node, time).setProperty(CHAPTER_PURCHASED_PRICE, price);
				}			
				res = "true";
			}
			else if(item_type.equalsIgnoreCase("S"))
			{
				Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
//				Index<Relationship> purchasedSeriesRelIndex = graphDb.index().forRelationships(USER_PURCHASED_SERIES_REL_INDEX);
				Node series_node = seriesId_index.get(SERIES_ID, item_id).getSingle();
				if(series_node == null)
					throw new KahaniyaCustomException("Series doesnot exists with given id : "+item_id);
				
				Node user_node = userId_index.get(USER_ID, user_id).getSingle();
				if(user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				
				if(isRelationExistsBetween(USER_PURCHASED_A_SERIES, user_node, series_node))
					throw new KahaniyaCustomException("User :"+user_id+" already purchased this chapter :"+item_id);
				else
				{
					Relationship rel = createRelation(USER_PURCHASED_A_SERIES, user_node, series_node, time);
					rel.setProperty(SERIES_PURCHASED_PRICE, price);
					
					graphDb.index().forNodes(PURCHASED_SERIES_ID_INDEX).remove(series_node, SERIES_ID, item_id);
					graphDb.index().forNodes(PURCHASED_SERIES_ID_INDEX).add(series_node, SERIES_ID, item_id);
				}			
				res = "true";
			}
			else if(item_type.equalsIgnoreCase("AN"))
			{
				Index<Node> anthologyId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
//				Index<Relationship> purchasedAnthologyRelIndex = graphDb.index().forRelationships(USER_PURCHASED_ANTHOLOGY_REL_INDEX);
				Node anthology_node = anthologyId_index.get(ANTHOLOGY_ID, item_id).getSingle();
				if(anthology_node == null)
					throw new KahaniyaCustomException("Anthology doesnot exists with given id : "+item_id);
				
				Node user_node = userId_index.get(USER_ID, user_id).getSingle();
				if(user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				
				if(isRelationExistsBetween(USER_PURCHASED_A_ANTHOLOGY, user_node, anthology_node))
					throw new KahaniyaCustomException("User :"+user_id+" already purchased this chapter :"+item_id);
				else
				{
					Relationship rel = createRelation(USER_PURCHASED_A_ANTHOLOGY, user_node, anthology_node, time);
					rel.setProperty(ANTHOLOGY_PURCHASED_PRICE, price);
//					purchasedAnthologyRelIndex.add(rel, USER_PURCHASED_ANTHOLOGY_REL_INDEX, user_id+"_purchased_"+item_id);
				}			
				res = "true";
			}
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ purchase_chapter()");
			System.out.println("Something went wrong, while purchasing a chapter from purchase_chapter  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ purchase_chapter()");
			System.out.println("Something went wrong, while purchasing a chapter from purchase_chapter  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}


	@Override
	public String recored_chapter_view(String chapter_id, String series_id,
			String user_id, int time, int view_count) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null)
				user_id = "";
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");

			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Relationship> userViewedChapterRelIndex = graphDb.index().forRelationships(USER_VIEWED_CHAPTER_REL_INDEX);
			
			Node chapter_node = chapterId_index.get(CHAPTER_ID, chapter_id).getSingle();
			if(chapter_node == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
			
//			int totalView = Integer.parseInt(chapter_node.getProperty(TOTAL_CHAPTER_VIEWS).toString());
			chapter_node.setProperty(TOTAL_CHAPTER_VIEWS, view_count);
			chapter_node.setProperty(CHAPTER_LAST_VIEWED_TIME, (int)(new Date().getTime()/1000));
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
			{
				tx.success();
				return "true";
			}
			
			Relationship rel = userViewedChapterRelIndex.get(USER_VIEWED_A_CHAPTER_ID, user_id+"_view_"+chapter_id).getSingle();
			if(rel != null)
			{
				rel.setProperty(TIME_CREATED, time);
				rel.setProperty(CHAPTER_VIEWS, Integer.parseInt(rel.getProperty(CHAPTER_VIEWS).toString())+1);
			}
			else
			{
				rel = createRelation(USER_VIEWED_A_CHAPTER, user_node, chapter_node, time);
				rel.setProperty(CHAPTER_VIEWS, 1);
				userViewedChapterRelIndex.add(rel, USER_VIEWED_A_CHAPTER_ID, user_id+"_view_"+chapter_id);
			}			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_chapter_view()");
			System.out.println("Something went wrong, while viewing a chapter from record_chapter_view  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_chapter_view()");
			System.out.println("Something went wrong, while voewing a chapter from record_chapter_view  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String record_nanostory_view(String nanostory_id, String user_id, 
			int time, int view_count) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null)
				user_id = "";
			if(nanostory_id == null || nanostory_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param nanostory_id");

			aquireWriteLock(tx);
			Index<Node> nanostoryId_index = graphDb.index().forNodes(NANOSTORY_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			
			Node nanostory_node = nanostoryId_index.get(NANOSTORY_ID, nanostory_id).getSingle();
			if(nanostory_node == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+nanostory_id);
			
//			int totalView = Integer.parseInt(chapter_node.getProperty(TOTAL_CHAPTER_VIEWS).toString());
			nanostory_node.setProperty(NANOSTORY_VIEWS, view_count);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
			{
				tx.success();
				return "true";
			}
			
			if(!isRelationExistsBetween(USER_VIEWED_A_NANOSTORY, user_node, nanostory_node)){
				createRelation(USER_VIEWED_A_NANOSTORY, user_node, nanostory_node, time);
			}
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_chapter_view()");
			System.out.println("Something went wrong, while viewing a chapter from record_chapter_view  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_chapter_view()");
			System.out.println("Something went wrong, while voewing a chapter from record_chapter_view  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}


	@Override
	public String recored_chapter_read(String chapter_id, String series_id,
			String user_id, int time, int read_status, int read_word_count) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(user_id == null)
				user_id = "";
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param chapter_id");

			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Relationship> userViewedChapterRelIndex = graphDb.index().forRelationships(USER_READ_CHAPTER_REL_INDEX);
			
			Node chapter_node = chapterId_index.get(CHAPTER_ID, chapter_id).getSingle();
			if(chapter_node == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);
			
			int totalReads = Integer.parseInt(chapter_node.getProperty(TOTAL_CHAPTER_READS).toString());
			if(read_status == 2)
				chapter_node.setProperty(TOTAL_CHAPTER_READS, totalReads + 1);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			if(user_node == null)
			{
				tx.success();
				return "true";
			}
			
			Relationship rel = userViewedChapterRelIndex.get(USER_READ_A_CHAPTER_ID, user_id+"_read_"+chapter_id).getSingle();
			if(rel != null)
			{
				rel.setProperty(CHAPTER_READ_WORD_COUNT, read_word_count);
				if(Integer.parseInt(rel.getProperty(CHAPTER_READ_STATUS).toString()) != 2){
					rel.setProperty(CHAPTER_READ_STATUS, read_status);
					if(isRelationExistsBetween(USER_BOOKMARK_CHAPTER, user_node, chapter_node))
						deleteRelation(USER_BOOKMARK_CHAPTER, user_node, chapter_node);
				}
			}
			else
			{
				rel = createRelation(USER_READ_A_CHAPTER, user_node, chapter_node, time);
				rel.setProperty(CHAPTER_READ_WORD_COUNT, read_word_count);
				rel.setProperty(CHAPTER_READ_STATUS, read_status);
				userViewedChapterRelIndex.add(rel, USER_READ_A_CHAPTER_ID, user_id+"_read_"+chapter_id);
			}			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_chapter_read()");
			System.out.println("Something went wrong, while reading a chapter from record_chapter_read  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ record_chapter_read()");
			System.out.println("Something went wrong, while reading a chapter from record_chapter_read  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}

	@Override
	public String get_feed(String tileType, String feedType, String filter,
			int prev_cnt, int count, String s_user_id, String genre, String lang, String user_id, String contestId) throws TException {
		if(feedType == null)
			feedType = "";
		if(tileType == null)
			tileType = "";
		if(filter == null)
			filter = "";
		if(user_id == null)
			user_id = "";
		if(s_user_id == null || s_user_id.isEmpty())
			s_user_id = "";
		if(genre == null)
			genre = "";
		if(lang == null)
			lang = "";
		
		if(feedType.equalsIgnoreCase("R"))
		{
			/*if(prev_cnt == 0)
				return get_recommended(filter, prev_cnt, count, s_user_id);
			else return get_recomended_by_series(filter, prev_cnt - 1, count, s_user_id);
			*/
			return get_for_you_feed(filter, prev_cnt, count, s_user_id).toString();
		}
		else if(tileType.equalsIgnoreCase("SEC"))
		{						
			if(user_id !=""){
//				System.out.println("in User_profile section");				
				switch(feedType) {
		         case "WC" :
		        	 return get_user_chapters_feed("W", filter, prev_cnt, count, s_user_id, "", "", user_id, "").toString();
		         case "WS" :
		        	 return get_user_series_feed("W", filter, prev_cnt, count, s_user_id, "", "", user_id).toString();
		         case "WAN" :
		        	 return get_user_anthologies_feed("W", filter, prev_cnt, count, s_user_id, "", "", user_id).toString();
		         case "WNS" :
		        	 return get_nanostories("WNS", filter, prev_cnt, count, s_user_id, "", "", user_id,"").toString();
		         default :
		        	 return get_user_profile_feed(filter, s_user_id, user_id, prev_cnt, count);
		        	 }
			}
			else if(s_user_id == ""){
				switch(feedType) {
		         case "TA" :
		        	 return get_popular_authors(filter, prev_cnt, count).toString();		            
		         case "TC" :
		        	 return get_popular_stories(filter, prev_cnt, count).toString();
		         case "TS" :
		        	 return get_popular_serieses(filter, prev_cnt, count).toString();
		         case "TCL" :
		        	 return get_justin_stories(filter, prev_cnt, count).toString();
		         case "TNS" :
		        	 return get_nanostories("ALL", filter, prev_cnt, count, "", "", "", "","").toString();
		         case "TAN" :
		        	 return get_popular_anthologies(filter, prev_cnt, count).toString();
		         case "TCP" :
		        	 return get_latest_purchased_stories(filter, prev_cnt, count).toString();
		         case "TG" :
		         case "TG_roamnce" :		        	         
		         case "TG_children" :		        	 
		         case "TG_fiction" :
		        	 return get_genre_feed(filter, prev_cnt, count).toString();	 
		         default :		        	 
		        	 return get_anonymous_feed(filter, prev_cnt, count);
		        	 }
			}
			else {
//				System.out.println("in User_feed section");
				switch(feedType) {
		         case "CR" :
		        	 return get_reading_feed(filter, prev_cnt, count, s_user_id).toString();
		         case "R" :
		        	 return get_for_you_feed(filter, prev_cnt, count, s_user_id).toString();
		         case "T" :
		        	 return get_trending_feed(filter, prev_cnt, count, s_user_id).toString();
		         case "S" :
		        	 return get_recommended_serieses(filter, prev_cnt, count, s_user_id).toString();
		         case "A" :
		        	 return get_recommended_authors(filter, prev_cnt, count, s_user_id).toString();
		         case "TNS" :
		        	 return get_nanostories("ALL", filter, prev_cnt, count, s_user_id, "", "", "","").toString();
		         case "TA" :
		        	 return get_popular_authors(filter, prev_cnt, count).toString();		            
		         case "TC" :
		        	 return get_popular_stories(filter, prev_cnt, count).toString();
		         case "TS" :
		        	 return get_popular_serieses(filter, prev_cnt, count).toString();
		         case "TAN" :
		        	 return get_popular_anthologies(filter, prev_cnt, count).toString();
		         case "TG" :
		        	 return get_genre_feed(filter, prev_cnt, count).toString();
		         case "TCL" :
		        	 return get_justin_stories(filter, prev_cnt, count).toString();
		         default :
		        	 return get_user_feed(filter, s_user_id, prev_cnt, count);
		        	 }
			}
		}	
		else if(tileType.equalsIgnoreCase("A")) 
			return get_authors(feedType, filter, prev_cnt, count, s_user_id);
		else if(tileType.equalsIgnoreCase("CN"))
			return get_contests(feedType, filter, prev_cnt, count, s_user_id);
		else if(tileType.equalsIgnoreCase("S"))
			return get_series(feedType, filter, prev_cnt, count, s_user_id, genre, lang, user_id, contestId);
		else if(tileType.equalsIgnoreCase("NS"))
			return get_nanostories(feedType, filter, prev_cnt, count, s_user_id, genre, lang, user_id, contestId).toString();
		else if(tileType.equalsIgnoreCase("AN"))
			return get_anthologies(feedType, filter, prev_cnt, count, s_user_id, genre, lang, user_id);
		else if(tileType.equalsIgnoreCase("C"))
			return get_chapters(feedType, filter, prev_cnt, count, s_user_id, genre, lang, user_id, contestId);
		else if(tileType.equalsIgnoreCase("All"))
			return "[{\"chapters\":"+get_chapters(feedType, filter, prev_cnt, count, s_user_id, genre, lang, user_id, "")+", \"series\":"+ get_series(feedType, filter, prev_cnt, count, s_user_id, genre, lang, user_id, contestId)+", \"authors\":"+get_authors(feedType, filter, prev_cnt, count, s_user_id)+"}]";
		else return "";
	}
	
	// 1: user 2: series 3: story 4: anthology 5: Nanostory
	
	private String get_user_profile_feed(String filter, String s_user_id, String user_id, int prev_cnt, int count){
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			
			JSONObject uChapters = new JSONObject();
			uChapters.put("tp", 3);
			uChapters.put("data", get_user_chapters_feed("W", filter, prev_cnt, count, s_user_id, "", "", user_id, ""));
			uChapters.put("ftp", "WC");
			
			JSONObject uSeries = new JSONObject();
			uSeries.put("tp", 2);
			uSeries.put("data", get_user_series_feed("W", filter, prev_cnt, count, s_user_id, "", "", user_id));
			uSeries.put("ftp", "WS");
			
			JSONObject uAnthology = new JSONObject();
			uAnthology.put("tp", 4);
			uAnthology.put("data", get_user_anthologies_feed("W", filter, prev_cnt, count, s_user_id, "", "", user_id));
			uAnthology.put("ftp", "WAN");
			
			JSONObject uNanostories = new JSONObject();
			uNanostories.put("tp", 5);
			uNanostories.put("data", get_nanostories("WNS", filter, prev_cnt, count, s_user_id, "", "", user_id,""));
			uNanostories.put("ftp", "WNS");
					
			
			jsonArray.put(uChapters);
			jsonArray.put(uNanostories);
			jsonArray.put(uSeries);
			jsonArray.put(uAnthology);
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_profile_sections()");
			System.out.println("Something went wrong, while returning chapters from get_profile_sections()  :"+ex.getMessage());
				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_profile_sections()");
			System.out.println("Something went wrong, while returning chapters from get_profile_sections()  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();
		
	}
	
	
	private String get_user_feed(String filter, String user_id, int prev_cnt, int count){
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			
			
			Index<Node> user_index = graphDb.index().forNodes(USER_ID_INDEX);
			Node user = user_index.get(USER_ID, user_id).getSingle();
			if(user == null )
				throw new KahaniyaCustomException("User doesnot exists with the given id"+ user_id + "What to do");
			
			int c= 6;
			if(prev_cnt < 6){
				JSONObject inReading = new JSONObject();
				inReading.put("tp", 3);
				inReading.put("data", get_reading_feed(filter, 0, c, user_id));
				inReading.put("ftp", "CR");
				
				JSONObject forYou = new JSONObject();
				forYou.put("tp", 3);
				forYou.put("data", get_for_you_feed(filter, 0, c, user_id));
				forYou.put("ftp", "R");
				
				JSONObject justIn = new JSONObject();
				justIn.put("tp", 3);
				justIn.put("data", get_justin_stories(filter, 0, c));
				justIn.put("ftp", "TCL");
				
				JSONObject nanoStories = new JSONObject();
				nanoStories.put("tp", 5);
				nanoStories.put("data", get_nanostories("ALL", filter, prev_cnt, count, user_id, "", "", "",""));
				nanoStories.put("ftp", "TNS");
							
				
				JSONObject inTrending = new JSONObject();
				JSONObject inSerieses = new JSONObject();
				JSONObject inAuthors = new JSONObject();
				
				Iterator<Relationship> followingUsersRel = user.getRelationships(USER_FOLLOW_USER, Direction.OUTGOING).iterator();
				
				int size = 0;
				
				while(followingUsersRel.hasNext())
				{
					if(size >= 5)
						break;
					Relationship rel = followingUsersRel.next();
					size++;
				}
							
				if(size >= 5)				
				{			
					inTrending.put("tp", 3);
					inTrending.put("data", get_trending_feed(filter, 0, c, user_id));
					inTrending.put("ftp", "T");

					
					inSerieses.put("tp", 2);
					inSerieses.put("data", get_recommended_serieses(filter, 0, c, user_id));
					inSerieses.put("ftp", "S");

					
					inAuthors.put("tp", 1);
					inAuthors.put("data", get_recommended_authors(filter, 0, c, user_id));
					inAuthors.put("ftp", "A");
				}
				else{
					inTrending.put("tp", 3);
					inTrending.put("data", get_popular_stories(filter, 0, c));
					inTrending.put("ftp", "TC");

					inSerieses.put("tp", 2);
					inSerieses.put("data", get_popular_serieses(filter, 0, c));
					inSerieses.put("ftp", "TS");

					inAuthors.put("tp", 1);
					inAuthors.put("data", get_popular_authors(filter, 0, c));
					inAuthors.put("ftp", "TA");
				}
				
				jsonArray.put(inReading);
				jsonArray.put(forYou);				
				jsonArray.put(nanoStories);
				jsonArray.put(justIn);
				jsonArray.put(inTrending);
				jsonArray.put(inSerieses);
				jsonArray.put(inAuthors);
			}
						
						
			if(prev_cnt > 6)
			{
				Iterator<Relationship> genres_itr = user.getRelationships(USER_INTERESTED_GENRE).iterator();
				LinkedList<Node> genres = new LinkedList<Node>();
				while(genres_itr.hasNext())
				{
					genres.addLast(genres_itr.next().getEndNode());
				}
				for(Node gen : genres)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					String ge_name = gen.getProperty(GENRE_NAME).toString();
					JSONObject gen2= new JSONObject();

					JSONObject JSONFilter = new JSONObject(filter);
					JSONFilter.put("genre", ge_name);

					gen2.put("tp", 3);
					gen2.put("data", get_genre_feed(JSONFilter.toString(), 0, c));
					gen2.put("ftp", "TG"+"_"+ge_name);
					jsonArray.put(gen2);
					c++;
				}
				
			}
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();
	}

	
	private JSONArray get_recommended_authors(String filter, int prev_cnt, int count, String user_id)
			throws TException {
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			System.out.println("in Rec Authors section");
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Empty value receieved for the parameter user_id");
			
			Index<Node> user_index = graphDb.index().forNodes(USER_ID_INDEX);
			Node user = user_index.get(USER_ID, user_id).getSingle();
			if(user == null )
				throw new KahaniyaCustomException("User doesnot exists with the given id");
			LinkedList<Node> followUsers = new LinkedList<Node>();
			
			Iterator<Relationship> usrViewedChaptersRelsItr = user.getRelationships(USER_VIEWED_A_CHAPTER).iterator();
			LinkedList<Relationship> usrViewedChaptersRelsList = new LinkedList<Relationship>();
						
			while(usrViewedChaptersRelsItr.hasNext())
				usrViewedChaptersRelsList.addLast(usrViewedChaptersRelsItr.next());
			Collections.sort(usrViewedChaptersRelsList, TimeCreatedComparatorForRelationships);
			
			int c = 0;
			for(Relationship rel : usrViewedChaptersRelsList)
			{
				Node author = rel.getEndNode().getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
				if(!isRelationExistsBetween(USER_FOLLOW_USER, user, author) && !followUsers.contains(author) && !user.equals(author)){
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					followUsers.addLast(author);
				}
			}
		
						
			// TO DO: Updating this call
			

//			Iterator<Relationship> followingUsersRel = user.getRelationships(USER_FOLLOW_USER, Direction.OUTGOING).iterator();
//			
//			LinkedList<Relationship> discovery_rels = new LinkedList<Relationship>();
//			LinkedList<Node> totalChapters = new LinkedList<Node>();
//											
//			
//			while(followingUsersRel.hasNext())				
//			{
//				Node following_user = followingUsersRel.next().getOtherNode(user);
//				Iterator<Relationship> followers = following_user.getRelationships(USER_FOLLOW_USER, Direction.OUTGOING).iterator();
//				while(followers.hasNext())
//				{
//					Relationship rel = followers.next();
//					if(!rel.getEndNode().equals(user))
//					{
//						Node f_user = rel.getEndNode();
//						if(!followUsers.contains(f_user))
//						{
//							followUsers.addLast(f_user);
//							//get latest user from followers list
//							Iterator<Relationship> itr = f_user.getRelationships(USER_FOLLOW_USER, Direction.INCOMING).iterator();
//							LinkedList<Relationship> list = new LinkedList<Relationship>();
//							while(itr.hasNext())
//							{
//								Relationship t_rel = itr.next();
//								if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
//									list.addLast(t_rel);
//							}
//							Collections.sort(list, TimeCreatedComparatorForRelationships);
//							if(!isRelationExistsBetween(USER_FOLLOW_USER, user, rel.getEndNode())
//									&& list.size() > 0 && !totalChapters.contains(rel.getEndNode()))
//							{
//								discovery_rels.addLast(list.getFirst());
//								totalChapters.add(rel.getEndNode());
//							}
//						}
//					}
//				}			
//
//			}
//			
////			if(discovery_rels.isEmpty())
////				return get_popular_authors(filter, 0, 6);	
//			
//			Collections.sort(discovery_rels, TimeCreatedComparatorForRelationships);
//			
//			for(Relationship rel : discovery_rels)
//			{
//				
//				if(c < prev_cnt)
//				{
//					c ++;
//					continue;
//				}
//				if(c >= count + prev_cnt)
//					break;
//				jsonArray.put(getJSONForDiscoveryRel(rel, user));
//				c++;
//			}
			
			for(Node f_user : followUsers)
				jsonArray.put(getJSONForUser(f_user,null));
			
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ discovery_feed()");
			System.out.println("Something went wrong, while returning discovery feed   :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ discovery_feed()");
			System.out.println("Something went wrong, while returning discovery feed   :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;	

	}

	
	private JSONArray get_recommended_serieses(String filter, int prev_cnt, int count, String user_id)throws TException 
	{
		JSONArray jsonArray = new JSONArray();
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);

			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Empty value receieved for the parameter user_id");
			
			Index<Node> user_index = graphDb.index().forNodes(USER_ID_INDEX);
			Node user = user_index.get(USER_ID, user_id).getSingle();
			
			if(user == null )
				throw new KahaniyaCustomException("User doesnot exists with the given id");
				
			Iterator<Relationship> followingUsersRel = user.getRelationships(USER_FOLLOW_USER, Direction.OUTGOING).iterator();
			
			LinkedList<Relationship> discovery_rels = new LinkedList<Relationship>();
			LinkedList<Node> totalChapters = new LinkedList<Node>();

			LinkedList<Node> subscribedSeries = new LinkedList<Node>();
			LinkedList<Node> reviewedSeries = new LinkedList<Node>();
			
			while(followingUsersRel.hasNext())				
			{
				Node following_user = followingUsersRel.next().getOtherNode(user);
				
				Iterator<Relationship> reviews = following_user.getRelationships(USER_WRITTEN_A_REVIEW, Direction.OUTGOING).iterator();
				while(reviews.hasNext())
				{
					Relationship rel = reviews.next();
					if(rel.getEndNode().hasRelationship(REVIEW_BELONGS_TO_SERIES))
					{
						Node r_series = rel.getEndNode().getSingleRelationship(REVIEW_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();						if(!reviewedSeries.contains(r_series) && !totalChapters.contains(r_series))
						{
							reviewedSeries.addLast(r_series);
							//get later user from followers list
							Iterator<Relationship> itr = r_series.getRelationships(REVIEW_BELONGS_TO_SERIES, Direction.INCOMING).iterator();
							LinkedList<Relationship> list = new LinkedList<Relationship>();
							while(itr.hasNext())
							{
								Relationship t_rel = itr.next();
								if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_REVIEW, Direction.INCOMING).getStartNode()))
									list.addLast(t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_REVIEW, Direction.INCOMING));
							}
							Collections.sort(list, TimeCreatedComparatorForRelationships);
							if(!isRelationExistsBetween(USER_SUBSCRIBED_TO_SERIES, user, rel.getEndNode())
									&& list.size() > 0 && !totalChapters.contains(rel.getEndNode()) && !isRelationExistsBetween(USER_STARTED_SERIES, user,  rel.getEndNode()))
							{							
								discovery_rels.addLast(list.getFirst());
								totalChapters.add(list.getFirst().getEndNode());
							}
						}
					}
				}
				Iterator<Relationship> sub_series = following_user.getRelationships(USER_SUBSCRIBED_TO_SERIES, Direction.OUTGOING).iterator();
				while(sub_series.hasNext())
				{
					Relationship rel = sub_series.next();
					Node s_series = rel.getEndNode();
					if(!subscribedSeries.contains(s_series) && !totalChapters.contains(s_series))
					{
						subscribedSeries.addLast(s_series);
						//get latest user from followers list
						Iterator<Relationship> itr = s_series.getRelationships(USER_SUBSCRIBED_TO_SERIES, Direction.INCOMING).iterator();
						LinkedList<Relationship> list = new LinkedList<Relationship>();
						while(itr.hasNext())
						{
							Relationship t_rel = itr.next();
							if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
								list.addLast(t_rel);
						}
						Collections.sort(list, TimeCreatedComparatorForRelationships);
						if(!isRelationExistsBetween(USER_SUBSCRIBED_TO_SERIES, user, rel.getEndNode())
								&& list.size() > 0 && !isRelationExistsBetween(USER_STARTED_SERIES, user, rel.getEndNode())){
							discovery_rels.addLast(list.getFirst());
							totalChapters.add(list.getFirst().getEndNode());
						}
					}
				}
				
			}
			

//			if(discovery_rels.isEmpty())
//				return get_popular_serieses(filter, 0, 6);
				
			Collections.sort(discovery_rels, TimeCreatedComparatorForRelationships);
			
			int c = 0;
			for(Relationship rel : discovery_rels)
			{
				
				if(c < prev_cnt)
				{
					c ++;
					continue;
				}
				if(c >= count + prev_cnt)
					break;
				jsonArray.put(getJSONForDiscoveryRel(rel, user));
				c++;
			}
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ discovery_feed()");
			System.out.println("Something went wrong, while returning discovery feed   :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ discovery_feed()");
			System.out.println("Something went wrong, while returning discovery feed   :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}

		
		return jsonArray;
	}
		
	private JSONArray get_trending_feed(String filter, int prev_cnt, int count, String user_id)
				throws TException {
			JSONArray jsonArray = new JSONArray();		
			try(Transaction tx = graphDb.beginTx())
			{
				aquireWriteLock(tx);

				if(user_id == null || user_id.length() == 0)
					throw new KahaniyaCustomException("Empty value receieved for the parameter user_id");
				
				Index<Node> user_index = graphDb.index().forNodes(USER_ID_INDEX);
				Node user = user_index.get(USER_ID, user_id).getSingle();
				if(user == null )
					throw new KahaniyaCustomException("User doesnot exists with the given id");
				
//				int i = 0;			
				Iterator<Relationship> followingUsersRel = user.getRelationships(USER_FOLLOW_USER, Direction.OUTGOING).iterator();
				
				LinkedList<Relationship> discovery_rels = new LinkedList<Relationship>();
				
								
				LinkedList<Node> totalChapters = new LinkedList<Node>();

				LinkedList<Node> readChapters = new LinkedList<Node>();
				LinkedList<Node> purchasedChapters = new LinkedList<Node>();
				LinkedList<Node> ratedChapters = new LinkedList<Node>();
				LinkedList<Node> favChapters = new LinkedList<Node>();
				LinkedList<Node> commentedChapters = new LinkedList<Node>();
				LinkedList<Node> authorChapters = new LinkedList<Node>();
				
				
				while(followingUsersRel.hasNext())				
				{
					Node following_user = followingUsersRel.next().getOtherNode(user);
	/*				Iterator<Relationship> series = following_user.getRelationships(USER_STARTED_SERIES, Direction.OUTGOING).iterator();
					while(series.hasNext())
					{
						Relationship rel = series.next();
						if(rel.getEndNode().getProperty(SERIES_TYPE).toString().equals("2"))
							discovery_rels.addLast(rel);
					}
	*/
					Iterator<Relationship> fav_chapters = following_user.getRelationships(USER_FAV_CHAPTER, Direction.OUTGOING).iterator();
					while(fav_chapters.hasNext())
					{
						Relationship rel = fav_chapters.next();
						Node fav_chapter = rel.getEndNode();
						if(!favChapters.contains(fav_chapter))
						{
							favChapters.addLast(fav_chapter);
							//get latest user from followers list
							Iterator<Relationship> itr = fav_chapter.getRelationships(USER_FAV_CHAPTER, Direction.INCOMING).iterator();
							LinkedList<Relationship> list = new LinkedList<Relationship>();
							while(itr.hasNext())
							{
								Relationship t_rel = itr.next();
								if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
									list.addLast(t_rel);
							}
							Collections.sort(list, TimeCreatedComparatorForRelationships);
							if(list.size() > 0 && !totalChapters.contains(rel.getEndNode()) && !isRelationExistsBetween(USER_WRITTEN_A_CHAPTER, user, rel.getEndNode())){
								discovery_rels.addLast(list.getFirst());
								totalChapters.add(rel.getEndNode());
							}
								
						}
					}
					
					Iterator<Relationship> pur_chapters = following_user.getRelationships(USER_PURCHASED_A_CHAPTER, Direction.OUTGOING).iterator();
					while(pur_chapters.hasNext())
					{
						Relationship rel = pur_chapters.next();

						//for every chapter view, there will a purchase with 0 or more coins, so skip 0 coins purchases
						if(Integer.parseInt(rel.getProperty(CHAPTER_PURCHASED_PRICE).toString()) <= 0)
							continue;
							
						Node pur_chapter = rel.getEndNode();
						if(!purchasedChapters.contains(pur_chapter) && !totalChapters.contains(pur_chapter))
						{
							purchasedChapters.addLast(pur_chapter);
							//get latest user from followers list
							Iterator<Relationship> itr = pur_chapter.getRelationships(USER_PURCHASED_A_CHAPTER, Direction.INCOMING).iterator();
							LinkedList<Relationship> list = new LinkedList<Relationship>();
							while(itr.hasNext())
							{
								Relationship t_rel = itr.next();
								if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
									list.addLast(t_rel);
							}
							Collections.sort(list, TimeCreatedComparatorForRelationships);
							if(list.size() > 0 && !isRelationExistsBetween(USER_WRITTEN_A_CHAPTER, user, rel.getEndNode())){
								discovery_rels.addLast(list.getFirst());
								totalChapters.add(rel.getEndNode());
							}							
						}
					}
					
					Iterator<Relationship> rated_chapters = following_user.getRelationships(USER_RATED_A_CHAPTER, Direction.OUTGOING).iterator();
					while(rated_chapters.hasNext())
					{
						Relationship rel = rated_chapters.next();
						Node rated_chapter = rel.getEndNode();
						if(!ratedChapters.contains(rated_chapter) && !totalChapters.contains(rated_chapter))
						{
							ratedChapters.addLast(rated_chapter);
							//get latest user from followers list
							Iterator<Relationship> itr = rated_chapter.getRelationships(USER_RATED_A_CHAPTER, Direction.INCOMING).iterator();
							LinkedList<Relationship> list = new LinkedList<Relationship>();
							while(itr.hasNext())
							{
								Relationship t_rel = itr.next();
								if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
									list.addLast(t_rel);
							}
							Collections.sort(list, TimeCreatedComparatorForRelationships);
							if(list.size() > 0 && !totalChapters.contains(rel.getEndNode())&& !isRelationExistsBetween(USER_WRITTEN_A_CHAPTER, user, rel.getEndNode())){
								discovery_rels.addLast(list.getFirst());
								totalChapters.add(rel.getEndNode());
							}							
						}
					}
					
					Iterator<Relationship> read_chapters = following_user.getRelationships(USER_READ_A_CHAPTER, Direction.OUTGOING).iterator();
					while(read_chapters.hasNext())
					{
						Relationship rel = read_chapters.next();
						Node read_chapter = rel.getEndNode();
						if(!readChapters.contains(read_chapter) && !totalChapters.contains(read_chapter))
						{
							readChapters.addLast(read_chapter);
							//get latest user from followers list
							Iterator<Relationship> itr = read_chapter.getRelationships(USER_READ_A_CHAPTER, Direction.INCOMING).iterator();
							LinkedList<Relationship> list = new LinkedList<Relationship>();
							while(itr.hasNext())
							{
								Relationship t_rel = itr.next();
								if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
									list.addLast(t_rel);
							}
							Collections.sort(list, TimeCreatedComparatorForRelationships);
							if(list.size() > 0 && !totalChapters.contains(rel.getEndNode())&& !isRelationExistsBetween(USER_WRITTEN_A_CHAPTER, user, rel.getEndNode())){
								discovery_rels.addLast(list.getFirst());
								totalChapters.add(rel.getEndNode());
							}							
						}
					}
					
					Iterator<Relationship> comments = following_user.getRelationships(USER_WRITTEN_A_COMMENT, Direction.OUTGOING).iterator();
					while(comments.hasNext())
					{
						Relationship rel = comments.next();
						if(rel.getEndNode().hasRelationship(COMMENT_WRITTEN_ON_CHAPTER) )
						{
							Node c_chapter = rel.getEndNode().getSingleRelationship(COMMENT_WRITTEN_ON_CHAPTER, Direction.OUTGOING).getEndNode();
							if(!commentedChapters.contains(c_chapter) && !totalChapters.contains(c_chapter))
							{
								commentedChapters.addLast(c_chapter);
								//get latest user from followers list
								Iterator<Relationship> itr = c_chapter.getRelationships(COMMENT_WRITTEN_ON_CHAPTER, Direction.INCOMING).iterator();
								LinkedList<Relationship> list = new LinkedList<Relationship>();
								while(itr.hasNext())
								{
									Relationship t_rel = itr.next();
									if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING).getStartNode()))
										list.addLast(t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING));
								}
								Collections.sort(list, TimeCreatedComparatorForRelationships);
								if(list.size() > 0 && !totalChapters.contains(rel.getEndNode())&& !isRelationExistsBetween(USER_WRITTEN_A_CHAPTER, user, rel.getEndNode())){
									discovery_rels.addLast(list.getFirst());
									totalChapters.add(rel.getEndNode());
								}							
							}
						}
					}
					
					Iterator<Relationship> authors_chapters = following_user.getRelationships(USER_WRITTEN_A_CHAPTER, Direction.OUTGOING).iterator();
					while(authors_chapters.hasNext())
					{
						Relationship rel = authors_chapters.next();
						Node authors_chapter = rel.getEndNode();
						if(!authorChapters.contains(authors_chapter) && !totalChapters.contains(authors_chapter) && !isRelationExistsBetween(USER_VIEWED_A_CHAPTER, user, authors_chapter))
						{
							authorChapters.addLast(authors_chapter);
							//get latest user from followers list
							Iterator<Relationship> itr = authors_chapter.getRelationships(USER_READ_A_CHAPTER, Direction.INCOMING).iterator();
							LinkedList<Relationship> list = new LinkedList<Relationship>();
							while(itr.hasNext())
							{
								Relationship t_rel = itr.next();
								if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
									list.addLast(t_rel);
							}
							Collections.sort(list, TimeCreatedComparatorForRelationships);
							if(list.size() > 0 && !totalChapters.contains(rel.getEndNode())&& !isRelationExistsBetween(USER_WRITTEN_A_CHAPTER, user, rel.getEndNode())){
								discovery_rels.addLast(list.getFirst());
								totalChapters.add(rel.getEndNode());
							}							
						}
					}

				}
				
//				if(discovery_rels.isEmpty())
//					return get_popular_stories(filter, 0, 6);
//								
				Collections.sort(discovery_rels, TimeCreatedComparatorForRelationships);
				
				int c = 0;
				for(Relationship rel : discovery_rels)
				{
					
					if(c < prev_cnt)
					{
						c ++;
						continue;
					}
					if(c >= count + prev_cnt)
						break;
					jsonArray.put(getJSONForDiscoveryRel(rel, user));
					c++;
				}
				tx.success();
			}
			catch(KahaniyaCustomException ex)
			{
				System.out.println(new Date().toString());
				System.out.println("Exception @ discovery_feed()");
				System.out.println("Something went wrong, while returning discovery feed   :"+ex.getMessage());
//					ex.printStackTrace();
				jsonArray = new JSONArray();
			}
			catch(Exception ex)
			{
				System.out.println(new Date().toString());
				System.out.println("Exception @ discovery_feed()");
				System.out.println("Something went wrong, while returning discovery feed   :"+ex.getMessage());
				ex.printStackTrace();
				jsonArray = new JSONArray();
			}
			return jsonArray;	

		}

	
	private JSONArray get_reading_feed(String filter, int prev_cnt, int count, String user_id ){
		
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
				int c = 0;
				Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
				
				Node user_node = userId_index.get(USER_ID, user_id).getSingle();
				
				if(user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				
				LinkedList<Node> countChaptersNode = new LinkedList<Node>();
				
				Iterator<Relationship> favCHaptersRelsItr = user_node.getRelationships(USER_BOOKMARK_CHAPTER).iterator();
				LinkedList<Relationship> favChaptersRelsList = new LinkedList<Relationship>();
				while(favCHaptersRelsItr.hasNext()){
					Relationship rel = favCHaptersRelsItr.next();
//					favChaptersRelsList.addLast(favCHaptersRelsItr.next());
					favChaptersRelsList.addLast(rel);
					countChaptersNode.add(rel.getEndNode());
				}
					
				
				Iterator<Relationship> isReadRels = user_node.getRelationships(USER_READ_A_CHAPTER, Direction.OUTGOING).iterator();
				while(isReadRels.hasNext())
				{
					Relationship rel = isReadRels.next();
					if(Integer.parseInt(rel.getProperty(CHAPTER_READ_STATUS).toString()) == 1 && !countChaptersNode.contains(rel.getEndNode()))
						favChaptersRelsList.addLast(rel);
				}
				
				Collections.sort(favChaptersRelsList, TimeCreatedComparatorForRelationships);
				LinkedList<Node> outputChaptersNode = new LinkedList<Node>();
								
				for(Relationship rel : favChaptersRelsList)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node chapter = rel.getEndNode();
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					outputChaptersNode.addLast(chapter);
				}
			
			for(Node chapter : outputChaptersNode)
				jsonArray.put(getJSONForChapter(chapter, user_node));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;

	}
		
	
	private String get_anonymous_feed(String filter, int prev_cnt, int count){
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			int c = 6;
			
			if(prev_cnt < 6)
			{
				JSONObject popularAuthors = new JSONObject();
				popularAuthors.put("tp",1);
				popularAuthors.put("data",get_popular_authors(filter, 0, c));
				popularAuthors.put("ftp","TA");
				
				JSONObject popularStories = new JSONObject();
				popularStories.put("tp",3);
				popularStories.put("data",get_popular_stories(filter, 0, c));
				popularStories.put("ftp","TC");
				
				JSONObject popularSerieses = new JSONObject();
				popularSerieses.put("tp",2);
				popularSerieses.put("data",get_popular_serieses(filter, 0, c));
				popularSerieses.put("ftp","TS");
				
				JSONObject popularNanoStories = new JSONObject();
				popularNanoStories.put("tp",5);
				popularNanoStories.put("data", get_nanostories("ALL", filter, prev_cnt, count, "", "", "", "",""));
				popularNanoStories.put("ftp","TNS");
								
				JSONObject justinStories = new JSONObject();
				justinStories.put("tp",3);
				justinStories.put("data",get_justin_stories(filter, 0, c));
				justinStories.put("ftp","TCL");
				
				JSONObject popularAnthologies = new JSONObject();
				popularAnthologies.put("tp",4);
				popularAnthologies.put("data",get_popular_anthologies(filter, 0, c));
				popularAnthologies.put("ftp","TAN");
				
//				JSONObject JSONFilter = new JSONObject(filter);
//				JSONObject romanceFeed = new JSONObject();
//				JSONFilter.put("genre", "romance");
//				romanceFeed.put("tp", 3);
//				romanceFeed.put("data", get_genre_feed(JSONFilter.toString(), 0, c));
//				romanceFeed.put("ftp", "TG_romance");
//				
				jsonArray.put(popularAuthors);
				jsonArray.put(popularStories);
				jsonArray.put(popularSerieses);
				jsonArray.put(popularNanoStories);
				jsonArray.put(popularAnthologies);
				jsonArray.put(justinStories);
//				jsonArray.put(romanceFeed);
			}
			
			if(prev_cnt >= 5)
			{
				int cnt = 5;
				Index<Node> genre_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
				ResourceIterator<Node> genres_itr = genre_index.query(GENRE_NAME, "*").iterator();
				LinkedList<Node> genres = new LinkedList<Node>();
				
				while(genres_itr.hasNext())
				{
					genres.addLast(genres_itr.next());
				}
				
				if(prev_cnt < genres.size() + 6){
					for(Node gen : genres)
					{
						if(cnt >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
							break;
						
						if(cnt < prev_cnt)
						{
							cnt++;
							continue;
						}
						String ge_name = "";
						ge_name = gen.getProperty(GENRE_NAME).toString();
						JSONObject gen2= new JSONObject();

						JSONObject JSONFilter = new JSONObject(filter);
						JSONFilter.put("genre", ge_name);

//						if(!(ge_name == "romance")){
							gen2.put("tp", 3);
							gen2.put("data", get_genre_feed(JSONFilter.toString(), 0, c));
							gen2.put("ftp", "TG"+"_"+ge_name);
							gen2.put("gnresize", genres.size());
							jsonArray.put(gen2);
//						}						
						cnt++;
					}					
				}				
			}

//			
//						
//			JSONObject JSONFilter = new JSONObject(filter);
//			
//			JSONFilter.put("genre", "romance");
//			JSONObject popularRomance = new JSONObject();
//       	 	popularRomance.put("tp",3);
//       	 	popularRomance.put("data", get_genre_feed(JSONFilter.toString(), 0, c));
//       	 	popularRomance.put("ftp","TG_romance");
//       	 	
//       	 	JSONFilter.remove("genre");
//       	 	JSONFilter.put("genre", "children");
//       	 	JSONObject popularChildren = new JSONObject();
//       	 	popularChildren.put("tp",3);
//       	 	popularChildren.put("data",get_genre_feed(JSONFilter.toString(), 0, c));
//       	 	popularChildren.put("ftp","TG_children");
//       	 
//       	 	JSONFilter.remove("genre");
//    	 	JSONFilter.put("genre", "fiction");
//       	 	JSONObject popularFiction = new JSONObject();
//    	 	popularFiction.put("tp",3);
//    	 	popularFiction.put("data",get_genre_feed(JSONFilter.toString(), 0, c));
//    	 	popularFiction.put("ftp","TG_fiction");
//    	 	
//			jsonArray.put(popularChildren);
//			jsonArray.put(popularFiction);
//			jsonArray.put(popularRomance);
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();
	}
	
	private JSONArray get_latest_purchased_stories(String filter, int prev_cnt, int count){
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
				LinkedList<Node> allChaptersList = new LinkedList<Node>();
				
				Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
				
				LinkedList<Node> outputChaptersNode = new LinkedList<Node>();
				Index<Node> chapter_id_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
				ResourceIterator<Node> allChapters = chapter_id_index.query(CHAPTER_ID, "*").iterator();
				
				while(allChapters.hasNext())
					allChaptersList.addLast(allChapters.next());
					
				Collections.sort(allChaptersList, TrendingComparatorForChapterNodes);
				int i = 0;
				for(Node chapter : allChaptersList)
				{
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						if(Integer.parseInt(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()) > 0)
							continue;
						if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
						i++;
						outputChaptersNode.addLast(chapter);
					}
						
				}
				
			
				for(Node chapter : outputChaptersNode)
				jsonArray.put(getJSONForChapter(chapter, null));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_latest_purchased_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_latest_purchased_chapters()  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_latest_purchased_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_latest_purchased_chapters()  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}
	
	private JSONArray get_justin_stories(String filter, int prev_cnt, int count){
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
				LinkedList<Node> allChaptersList = new LinkedList<Node>();
				
				Index<Node> genre_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
				Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
				
				LinkedList<Node> outputChaptersNode = new LinkedList<Node>();
				Index<Node> chapter_id_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
				ResourceIterator<Node> allChapters = chapter_id_index.query(CHAPTER_ID, "*").iterator();
				
				while(allChapters.hasNext())
					allChaptersList.addLast(allChapters.next());
					
				Collections.sort(allChaptersList, TimeCreatedComparatorForNodes);
				int i = 0;
				for(Node chapter : allChaptersList)
				{
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(i < prev_cnt)
											{
												i++;
												continue;
											}
											i++;
											outputChaptersNode.addLast(chapter);
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
						i++;
						outputChaptersNode.addLast(chapter);
					}
						
				}
				
			
				for(Node chapter : outputChaptersNode)
				jsonArray.put(getJSONForChapter(chapter, null));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_justin_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_justin_chapters  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_justin_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_justin_chapters  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}
	
	private JSONArray get_genre_feed(String filter, int prev_cnt, int count){
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
				Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
				Index<Node> genre_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
				Index<Node> chapter_id_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
				ResourceIterator<Node> allChapters = chapter_id_index.query(CHAPTER_ID, "*").iterator();
				LinkedList<Node> allChaptersList = new LinkedList<Node>();
				
				while(allChapters.hasNext())
					allChaptersList.addLast(allChapters.next());
				Collections.sort(allChaptersList, TrendingComparatorForChapterNodes);
				
				LinkedList<Node> outputChaptersNode = new LinkedList<Node>();
				

				
				int i = 0;
				for(Node chapter : allChaptersList)
				{
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);	
						
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{

											if(i < prev_cnt)
											{
												i++;
												continue;
											}
											i++;
											outputChaptersNode.addLast(chapter);
	
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
						i++;
						outputChaptersNode.addLast(chapter);
					}
					
				}
			for(Node chapter : outputChaptersNode)
				jsonArray.put(getJSONForChapter(chapter, null));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}
	
	private JSONArray get_popular_anthologies(String filter, int prev_cnt, int count)
	{

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			int c = 0;
			LinkedList<Node> outputAnthologyNode = new LinkedList<Node>();

			JSONObject filterJSON = new JSONObject(filter);
				Node langNode = lang_index.get(LANG_NAME, filterJSON.getString("language").toLowerCase()).getSingle();
				if(langNode == null)
					throw new KahaniyaCustomException("Invalid Language Name");
				Iterator<Relationship> anthologyRelItr = langNode.getRelationships(ANTHOLOGY_BELONGS_TO_LANGUAGE).iterator();
				LinkedList<Relationship> anthologyRelList = new LinkedList<Relationship>();
				while(anthologyRelItr.hasNext())
					anthologyRelList.addLast(anthologyRelItr.next());
				Collections.sort(anthologyRelList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : anthologyRelList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node anthology = rel.getStartNode();
//					
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputAnthologyNode.addLast(anthology);			
				}
			
			for(Node anthology : outputAnthologyNode)
				jsonArray.put(getJSONForAnthologies(anthology, null));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_anthologies()");
			System.out.println("Something went wrong, while returning anthologies from get_anthologies  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_anthologies()");
			System.out.println("Something went wrong, while returning anthologies from get_anthologies  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}

	
	private JSONArray get_popular_stories(String filter, int prev_cnt, int count) {
		JSONArray jsonArray = new JSONArray();
		try (Transaction tx = graphDb.beginTx()) {
			aquireWriteLock(tx);
			Index<Node> chapter_id_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			LinkedList<Node> allChaptersList = new LinkedList<Node>();
			LinkedList<Node> outputChaptersNode = new LinkedList<Node>();
			
			
			JSONObject filterJSON = new JSONObject(filter);
			
			Node langNode = lang_index.get(LANG_NAME, filterJSON.getString("language").toLowerCase()).getSingle();
			
			Iterator<Relationship> pinnedChapters = langNode.getRelationships(CHAPTER_PINNED_TO_LANGUAGE, Direction.INCOMING).iterator();
			
			LinkedList<Relationship> list = new LinkedList<Relationship>();
			while(pinnedChapters.hasNext())
			{
				Relationship t_rel = pinnedChapters.next();
				list.addLast(t_rel);
			}
			Collections.sort(list, TimeCreatedComparatorForRelationships);
			int i = 0;
			
			for(Relationship rel : list)
			{				
				Node chapter = rel.getStartNode();
				
				if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
					break;
				if(i < prev_cnt)
				{
					i++;
					continue;
				}
				else
				{
					i++;
					outputChaptersNode.addLast(chapter);			
				}
			}
			
			ResourceIterator<Node> allChapters = chapter_id_index.query(CHAPTER_ID, "*").iterator();
			while(allChapters.hasNext())
				allChaptersList.addLast(allChapters.next());
			Collections.sort(allChaptersList, TrendingComparatorForChapterNodes);
						
			for (Node chapter : allChaptersList) {
				if (i >= prev_cnt + count) // break the loop, if we got enough // required nodes to return
					break;
				
				Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
				Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
				for (String lang : filterJSON.getString("language").split(",")) {
					if (langNode != null && seriesLangNode.equals(langNode) && series.getProperty(SERIES_TYPE).toString().equals("1")) {
						if (i < prev_cnt) {
							i++;
							continue;
						}
						i++;
						outputChaptersNode.addLast(chapter);
					}
				}

			}
			for (Node chapter : outputChaptersNode)
				jsonArray.put(getJSONForChapter(chapter, null));
			
			
			tx.success();

		} catch (KahaniyaCustomException ex) {
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
			// ex.printStackTrace();
			jsonArray = new JSONArray();
		} catch (Exception ex) {
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}
	
	private JSONArray get_popular_serieses(String filter, int prev_cnt, int count) {
		// no need to apply filter
		JSONArray jsonArray = new JSONArray();
		try (Transaction tx = graphDb.beginTx()) {
			aquireWriteLock(tx);
			
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Index<Node> seriesNodeIndex = graphDb.index().forNodes(SERIES_ID_INDEX );
			LinkedList<Node> outputSeriesNode = new LinkedList<Node>();
			int c = 0;
			
			JSONObject filterJSON = new JSONObject(filter);
			Node langNode = lang_index.get(LANG_NAME, filterJSON.getString("language").toLowerCase()).getSingle();
			Iterator<Relationship> pinnedSeries = langNode.getRelationships(SERIES_PINNED_TO_LANGUAGE).iterator();
			
			LinkedList<Relationship> list = new LinkedList<Relationship>();
			while(pinnedSeries.hasNext())
			{
				Relationship t_rel = pinnedSeries.next();
				list.addLast(t_rel);
			}
			Collections.sort(list, TimeCreatedComparatorForRelationships);
			
			for(Relationship rel : list)
			{
				Node series = rel.getStartNode();
				
				if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
					break;
				if(c < prev_cnt)
				{
					c++;
					continue;
				}
				else
				{
					c++;
					outputSeriesNode.addLast(series);			
				}
			}
			
				
			if (c < prev_cnt + count){
				ResourceIterator<Node> seriesNodesItr = seriesNodeIndex.query(SERIES_ID, "*").iterator();
				LinkedList<Node> seriesList = new LinkedList<Node>();
				
				while (seriesNodesItr.hasNext())
					seriesList.addLast(seriesNodesItr.next());
				Collections.sort(seriesList, TrendingComparatorForSeriesNodes);
				
				for (Node series : seriesList) {
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
					if (filter != null && !filter.equals("")) {
						
						if (filterJSON.has("language")) {
							for (String lang : filterJSON.getString("language").split(",")) {
								langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if (langNode != null && seriesLangNode.equals(langNode) && series.getProperty(SERIES_TYPE).toString().equals("2") && !outputSeriesNode.contains(series)) {
									if (c < prev_cnt) {
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);

								}
							}
						} else // i.e., no need to apply filter
						{
							if (c < prev_cnt) {
								c++;
								continue;
							}
							c++;
							if (series.getProperty(SERIES_TYPE).toString().equals("2") && !outputSeriesNode.contains(series))
								outputSeriesNode.addLast(series);

						}
					} else // i.e., no need to apply filter
					{
						if (c < prev_cnt) {
							c++;
							continue;
						}
						c++;
						if (series.getProperty(SERIES_TYPE).toString().equals("2") && !outputSeriesNode.contains(series))
							outputSeriesNode.addLast(series);

					}
				}
			}
						
			for (Node series : outputSeriesNode)
				jsonArray.put(getJSONForSeries(series, null));

			tx.success();

		} catch (KahaniyaCustomException ex) {
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_series()");
			System.out.println("Something went wrong, while returning series from get_series  :"
							+ ex.getMessage());
			// ex.printStackTrace();
			jsonArray = new JSONArray();
		} catch (Exception ex) {
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_series()");
			System.out.println("Something went wrong, while returning series from get_series  :"
							+ ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}

	private JSONArray get_for_you_feed(String filter, int prev_cnt, int count, String user_id)
	{
		// no need to apply filter
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);			
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();

			if(user_node == null)
				throw new KahaniyaCustomException("No user exists with given id : "+user_id);

			Iterator<Relationship> genres_itr = user_node.getRelationships(USER_INTERESTED_GENRE).iterator();					
			Iterator<Relationship> lang_itr = user_node.getRelationships(USER_INTERESTED_LANGUAGE).iterator();					
			
			LinkedList<Node> genres = new LinkedList<Node>();
			LinkedList<Node> langs = new LinkedList<Node>();
			
			LinkedList<Node> lang_genres = new LinkedList<Node>();

			while(genres_itr.hasNext())
			{
				genres.addLast(genres_itr.next().getEndNode());
			}
			while(lang_itr.hasNext())
			{
				langs.addLast(lang_itr.next().getEndNode());
			}
						
			for(Node gen : genres)
			{
				for(Node lang : langs)
				{
					Node n = genre_lang_index.get(GENRE_LANG_NAME, gen.getProperty(GENRE_NAME).toString() + " " + lang.getProperty(LANG_NAME).toString()).getSingle();
					if(n != null)
					{
						lang_genres.addLast(n);
					}
				}
			}

			LinkedList<Node> chapterList = new LinkedList<Node>();
			
			// chapters written by following authors
			Iterator<Relationship> authors = user_node.getRelationships(USER_FOLLOW_USER, Direction.OUTGOING).iterator();
			while(authors.hasNext())
			{
				Node author = authors.next().getEndNode();
				Iterator<Relationship> startedSeries = author.getRelationships(USER_STARTED_SERIES, Direction.OUTGOING).iterator();
				while(startedSeries.hasNext())
				{
					Node series = startedSeries.next().getEndNode();
					Iterator<Relationship> chaptersFromSeries = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
					while(chaptersFromSeries.hasNext())
					{
						//chapter should be from user interested lang
						Node chapter = chaptersFromSeries.next().getStartNode();
						if(langs.contains(chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode())
								&& !isRelationExistsBetween(USER_READ_A_CHAPTER, user_node, chapter))
							chapterList.addLast(chapter);
					}
				}
			}
			// chapters from subscribed series
			Iterator<Relationship> subscribedSeries = user_node.getRelationships(USER_SUBSCRIBED_TO_SERIES, Direction.OUTGOING).iterator();
			while(subscribedSeries.hasNext())
			{
				Node series = subscribedSeries.next().getEndNode();
				//no need to add chapters from series if user is already following the author, we added those chapters above
				if(isRelationExistsBetween(USER_FOLLOW_USER, user_node, series.getSingleRelationship(USER_STARTED_SERIES, Direction.INCOMING).getStartNode()))
					continue;
				
				Iterator<Relationship> chaptersFromSeries = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
				while(chaptersFromSeries.hasNext())
				{
					//chapter should be from user interested lang
					Node chapter = chaptersFromSeries.next().getStartNode();
					if(langs.contains(chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode())
							&& !isRelationExistsBetween(USER_READ_A_CHAPTER, user_node, chapter))
						chapterList.addLast(chapter);
				}
			}
			
			Collections.sort(chapterList, TimeCreatedComparatorForNodes);
			
			int c = 0;
			
			for(Node chapter : chapterList)
			{
				if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
					break;
				
				if(c < prev_cnt)
				{
					c++;
					continue;
				}
				c++;
				jsonArray.put(getJSONForChapter(chapter, user_node));				
			}
						
			//if sufficient chapters not found, add chapters from following genres with filters
			if(c < prev_cnt + count)
			{
				LinkedList<Node> allOtherChapterList = new LinkedList<Node>();
				for(Node n : lang_genres)
				{
					Iterator<Relationship> seriesRelItr = n.getRelationships(SERIES_BELONGS_TO_GENRE_LANGUAGE).iterator();
					while(seriesRelItr.hasNext())
					{
						Node seriesNode = seriesRelItr.next().getStartNode();
						if(isRelationExistsBetween(USER_SUBSCRIBED_TO_SERIES, user_node, seriesNode))
							break;
						if(isRelationExistsBetween(USER_FOLLOW_USER, user_node, seriesNode.getSingleRelationship(USER_STARTED_SERIES, Direction.INCOMING).getStartNode()))
							break;
						if(user_node.equals(seriesNode.getSingleRelationship(USER_STARTED_SERIES, Direction.INCOMING).getStartNode()))
							break;
						Iterator<Relationship> chapterRelItr = seriesNode.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
						while(chapterRelItr.hasNext())
						{
							Node chapter = chapterRelItr.next().getStartNode();
							if(!isRelationExistsBetween(USER_READ_A_CHAPTER, user_node, chapter))
								allOtherChapterList.addLast(chapter);
						}
					}
					
				}
				
				Collections.sort(allOtherChapterList, TrendingComparatorForChapterNodes);
				
				for(Node chapter : allOtherChapterList)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					jsonArray.put(getJSONForChapter(chapter, user_node));				
				}

			}
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_for_you_feed()");
			System.out.println("Something went wrong, while returning for you chapters from get_for_you_feed  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_for_you_feed()");
			System.out.println("Something went wrong, while returning for you chapters from get_for_you_feed  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}
	
	private String get_recommended(String filter, int prev_cnt, int count, String user_id)
	{

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			
			if(user_node == null)
				throw new KahaniyaCustomException("No user exists with given id : "+user_id);

			Iterator<Relationship> genres_itr = user_node.getRelationships(USER_INTERESTED_GENRE).iterator();					
			Iterator<Relationship> lang_itr = user_node.getRelationships(USER_INTERESTED_LANGUAGE).iterator();					
			
			LinkedList<Node> genres = new LinkedList<Node>();
			LinkedList<Node> langs = new LinkedList<Node>();
			
			LinkedList<Node> lang_genres = new LinkedList<Node>();

			while(genres_itr.hasNext())
			{
				genres.addLast(genres_itr.next().getEndNode());
			}
			while(lang_itr.hasNext())
			{
				langs.addLast(lang_itr.next().getEndNode());
			}
						
			for(Node gen : genres)
			{
				for(Node lang : langs)
				{
					Node n = genre_lang_index.get(GENRE_LANG_NAME, gen.getProperty(GENRE_NAME).toString() + " " + lang.getProperty(LANG_NAME).toString()).getSingle();
					if(n != null)
					{
						lang_genres.addLast(n);
					}
				}
			}
			
			LinkedList<Node> allChapterList = new LinkedList<Node>();
			
			for(Node n : lang_genres)
			{
				Iterator<Relationship> seriesRelItr = n.getRelationships(SERIES_BELONGS_TO_GENRE_LANGUAGE).iterator();
				while(seriesRelItr.hasNext())
				{
					Iterator<Relationship> chapterRelItr = seriesRelItr.next().getStartNode().getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
					while(chapterRelItr.hasNext())
						allChapterList.addLast(chapterRelItr.next().getStartNode());
					
				}
				
			}
			
			
			Collections.sort(allChapterList, TrendingComparatorForChapterNodes);

			JSONObject jRecObj = new JSONObject();
			JSONArray jRecArray = new JSONArray();

			LinkedList<Node> authors = new LinkedList<Node>();
			
/*			for(int i=0; i< 3; i++)
			{
				if(allChapterList.size() > i)
					jRecArray.put(getJSONForChapter(allChapterList.get(i), user_node));
			}
*/			
			for(Node chapterNode : allChapterList)
			{
				Node auth = chapterNode.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
				if( auth.equals(user_node) || !"2".equalsIgnoreCase(auth.getProperty(STATUS).toString()) || authors.contains(auth) || isRelationExistsBetween(USER_VIEWED_A_CHAPTER, user_node, chapterNode))
					continue;
				
				authors.addLast(auth);
				jRecArray.put(getJSONForChapter(chapterNode, user_node));

				if(authors.size() == 3)
					break;
			}

			if(jRecArray.length() > 0)
			{
				jRecObj.put("tp",0);
				jRecObj.put("data", jRecArray);

				jsonArray.put(jRecObj);
			} 
				
			// removed lang based recommended listing
/*			authors.clear();

			for(Node n : langs)
			{
				JSONObject jobj = new JSONObject();
				JSONArray jarray = new JSONArray();
				
				Iterator<Relationship> seriesRelItr = n.getRelationships(SERIES_BELONGS_TO_LANGUAGE).iterator();
				LinkedList<Node> chapterList = new LinkedList<Node>();
				while(seriesRelItr.hasNext())
				{
					Iterator<Relationship> chapterRelItr = seriesRelItr.next().getStartNode().getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
					while(chapterRelItr.hasNext())
					{
						chapterList.addLast(chapterRelItr.next().getStartNode());
					}
				}
				Collections.sort(chapterList, TimeCreatedComparatorForNodes);
				authors.clear();
				for(Node chapterNode : chapterList)
				{
					Node auth = chapterNode.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
					if( auth.equals(user_node) || authors.contains(auth))
						continue;
					
					authors.addLast(auth);
					jarray.put(getJSONForChapter(chapterNode, user_node));

					if(authors.size() == 3)
						break;
				}
				if(jarray.length() > 0)
				{
					jobj.put("tp",1);
					jobj.put("ttl", n.getProperty(LANG_NAME));
					jobj.put("data", jarray);

					jsonArray.put(jobj);
				}
				
			} */

			//TODO sort the lang_genres node list, so that if a story is added to genere now, it should be on top (remove the genre if the latest chapter is read)
			for(Node g : lang_genres)
			{
				JSONObject jobj = new JSONObject();
				JSONArray jarray = new JSONArray();
				
				Iterator<Relationship> seriesRelItr = g.getRelationships(SERIES_BELONGS_TO_GENRE_LANGUAGE).iterator();
				LinkedList<Node> chapterList = new LinkedList<Node>();
				while(seriesRelItr.hasNext())
				{
					Iterator<Relationship> chapterRelItr = seriesRelItr.next().getStartNode().getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
					while(chapterRelItr.hasNext())
					{
						chapterList.addLast(chapterRelItr.next().getStartNode());
					}
				}
				Collections.sort(chapterList, TimeCreatedComparatorForNodes);
				authors.clear();
				for(Node chapterNode : chapterList)
				{
					Node auth = chapterNode.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
					if( auth.equals(user_node) || !"2".equalsIgnoreCase(auth.getProperty(STATUS).toString()) || authors.contains(auth) || isRelationExistsBetween(USER_VIEWED_A_CHAPTER, user_node, chapterNode))
						continue;
					authors.addLast(auth);

					jarray.put(getJSONForChapter(chapterNode, user_node));
					if(authors.size() == 3)
						 break;
				}
				if(jarray.length() > 0)
				{
					jobj.put("tp",2);
					jobj.put("ttl", g.getProperty(GENRE_NAME));
					jobj.put("data", jarray);

					jsonArray.put(jobj);
				}
				
			}
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_recommended()");
			System.out.println("Something went wrong, while returning recommended chapters from get_recommended  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_recommended()");
			System.out.println("Something went wrong, while returning recommended chapters from get_recommended  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();
	}

	private String get_recomended_by_series(String filter, int prev_cnt, int count, String user_id)
	{

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> genre_lang_index = graphDb.index().forNodes(GENRE_LANG_INDEX);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			
			if(user_node == null)
				throw new KahaniyaCustomException("No user exists with given id : "+user_id);

			Iterator<Relationship> genres_itr = user_node.getRelationships(USER_INTERESTED_GENRE).iterator();					
			Iterator<Relationship> lang_itr = user_node.getRelationships(USER_INTERESTED_LANGUAGE).iterator();					
			
			LinkedList<Node> genres = new LinkedList<Node>();
			LinkedList<Node> langs = new LinkedList<Node>();
			
			LinkedList<Node> lang_genres = new LinkedList<Node>();

			while(genres_itr.hasNext())
			{
				genres.addLast(genres_itr.next().getEndNode());
			}
			while(lang_itr.hasNext())
			{
				langs.addLast(lang_itr.next().getEndNode());
			}
						
			for(Node gen : genres)
			{
				for(Node lang : langs)
				{
					Node n = genre_lang_index.get(GENRE_LANG_NAME, gen.getProperty(GENRE_NAME).toString() + " " + lang.getProperty(LANG_NAME).toString()).getSingle();
					if(n != null)
					{
						lang_genres.addLast(n);
					}
				}
			}
			
			LinkedList<Node> allChapterList = new LinkedList<Node>();
			LinkedList<Node> skipChapterList = new LinkedList<Node>();
			
			for(Node n : lang_genres)
			{
				Iterator<Relationship> seriesRelItr = n.getRelationships(SERIES_BELONGS_TO_GENRE_LANGUAGE).iterator();
				while(seriesRelItr.hasNext())
				{
					Iterator<Relationship> chapterRelItr = seriesRelItr.next().getStartNode().getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
					while(chapterRelItr.hasNext())
						allChapterList.addLast(chapterRelItr.next().getStartNode());
				}
				
			}
			
			Collections.sort(allChapterList, TrendingComparatorForChapterNodes);

			LinkedList<Node> authors = new LinkedList<Node>();
			
/*			for(int i=0; i< 3; i++)
			{
				if(allChapterList.size() > i)
					jRecArray.put(getJSONForChapter(allChapterList.get(i), user_node));
			}
*/			
			for(Node chapterNode : allChapterList)
			{
				Node auth = chapterNode.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
				if( auth.equals(user_node) || !"2".equalsIgnoreCase(auth.getProperty(STATUS).toString()) || authors.contains(auth) || isRelationExistsBetween(USER_VIEWED_A_CHAPTER, user_node, chapterNode))
					continue;
				
				authors.addLast(auth);
				skipChapterList.addLast(chapterNode);

				if(authors.size() == 3)
					break;
			}
				
			// removed lang based recommended listing
/*			authors.clear();

			for(Node n : langs)
			{
				
				Iterator<Relationship> seriesRelItr = n.getRelationships(SERIES_BELONGS_TO_LANGUAGE).iterator();
				LinkedList<Node> chapterList = new LinkedList<Node>();
				while(seriesRelItr.hasNext())
				{
					Iterator<Relationship> chapterRelItr = seriesRelItr.next().getStartNode().getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
					while(chapterRelItr.hasNext())
					{
						chapterList.addLast(chapterRelItr.next().getStartNode());
					}
				}
				Collections.sort(chapterList, TimeCreatedComparatorForNodes);
				authors.clear();
				for(Node chapterNode : chapterList)
				{
					Node auth = chapterNode.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
					if( auth.equals(user_node) || authors.contains(auth))
						continue;
					
					authors.addLast(auth);
					skipChapterList.addLast(chapterNode);

					if(authors.size() == 3)
						break;
				}
				
			} */

			//TODO sort the lang_genres node list, so that if a story is added to genere now, it should be on top (remove the genre if the latest chapter is read)
			for(Node g : lang_genres)
			{
				
				Iterator<Relationship> seriesRelItr = g.getRelationships(SERIES_BELONGS_TO_GENRE_LANGUAGE).iterator();
				LinkedList<Node> chapterList = new LinkedList<Node>();
				while(seriesRelItr.hasNext())
				{
					Iterator<Relationship> chapterRelItr = seriesRelItr.next().getStartNode().getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
					while(chapterRelItr.hasNext())
					{
						chapterList.addLast(chapterRelItr.next().getStartNode());
					}
				}
				Collections.sort(chapterList, TimeCreatedComparatorForNodes);
				authors.clear();
				for(Node chapterNode : chapterList)
				{
					Node auth = chapterNode.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
					if( auth.equals(user_node) || !"2".equalsIgnoreCase(auth.getProperty(STATUS).toString()) || authors.contains(auth) || isRelationExistsBetween(USER_VIEWED_A_CHAPTER, user_node, chapterNode))
						continue;
					authors.addLast(auth);

					skipChapterList.addLast(chapterNode);
					if(authors.size() == 3)
						 break;
				}
				
			}
			
			int c = 0;
			for(Node chapter: allChapterList)
			{
				if(c >= prev_cnt + count)
					break;
				if(skipChapterList.contains(chapter))
					continue;
				else if(c < prev_cnt)
				{
					c++;
					continue;
				}
				c++;
				jsonArray.put(getJSONForChapter(chapter, user_node));
			}
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_recomended_by_series()");
			System.out.println("Something went wrong, while returning recommended chapters from get_recomended_by_series  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_recomended_by_series()");
			System.out.println("Something went wrong, while returning recommended chapters from get_recomended_by_series  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();
	}
	
	private String get_series(String feedType, String filter, int prev_cnt, int count, String s_user_id, String genre_name, String lang_name, String user_id, String contestId)
	{

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> genre_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			Node s_user_node = userId_index.get(USER_ID, s_user_id).getSingle();
			
			int c = 0;
			LinkedList<Node> outputSeriesNode = new LinkedList<Node>();
			
			if(feedType.equalsIgnoreCase("SUB"))
			{
				if(s_user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+s_user_id);
				Iterator<Relationship> subscribedRelsItr = s_user_node.getRelationships(USER_SUBSCRIBED_TO_SERIES).iterator();
				LinkedList<Relationship> subscribedRelsList = new LinkedList<Relationship>();
				while(subscribedRelsItr.hasNext())
					subscribedRelsList.addLast(subscribedRelsItr.next());
				Collections.sort(subscribedRelsList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : subscribedRelsList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node series = rel.getEndNode();
					Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
					Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(c < prev_cnt)
											{
												c++;
												continue;
											}
											c++;
											outputSeriesNode.addLast(series);			
											
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
									
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputSeriesNode.addLast(series);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputSeriesNode.addLast(series);			
						
					}
					
				}
			}

			else if(feedType.equalsIgnoreCase("W"))
			{
				if(user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				Iterator<Relationship> subscribedRelsItr = user_node.getRelationships(USER_STARTED_SERIES).iterator();
				LinkedList<Relationship> subscribedRelsList = new LinkedList<Relationship>();
				while(subscribedRelsItr.hasNext())
					subscribedRelsList.addLast(subscribedRelsItr.next());
				Collections.sort(subscribedRelsList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : subscribedRelsList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node series = rel.getEndNode();
					
					if(!series.getProperty(SERIES_TYPE).toString().equals("2"))
						continue;
					
					Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
					Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(c < prev_cnt)
											{
												c++;
												continue;
											}
											c++;
											outputSeriesNode.addLast(series);			
											
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
									
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputSeriesNode.addLast(series);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputSeriesNode.addLast(series);			
						
					}
					
				}
			}
			

			else if(feedType.equalsIgnoreCase("OG")) // for ONGOING series
			{
				
				Index<Node> seriesStatusBaseNodeIndex = graphDb.index().forNodes( LOCK_INDEX );
				Node seriesOngoingNode = seriesStatusBaseNodeIndex.get(SERIES_STATUS, SERIES_ONGOING_BASE_NODE).getSingle();
				
				Iterator<Relationship> ongoignSeriesRelsItr = seriesOngoingNode.getRelationships(SERIES_ONGOING_REL_TO_BASE_NODE).iterator();
				LinkedList<Node> ongoignSeriesList = new LinkedList<Node>();
				while(ongoignSeriesRelsItr.hasNext())
					ongoignSeriesList.addLast(ongoignSeriesRelsItr.next().getStartNode());
				Collections.sort(ongoignSeriesList, LastChapterAddedTimeComparatorForSeries);				
				for(Node series : ongoignSeriesList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(!series.getProperty(SERIES_TYPE).toString().equals("2"))
						continue;
					
					Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
					Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(c < prev_cnt)
											{
												c++;
												continue;
											}
											c++;
											outputSeriesNode.addLast(series);			
											
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
									
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputSeriesNode.addLast(series);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputSeriesNode.addLast(series);			
						
					}
					
				}
			}
			else if(feedType.equalsIgnoreCase("CM")) // for COMPLETED series
			{
				
				Index<Node> seriesStatusBaseNodeIndex = graphDb.index().forNodes( LOCK_INDEX );
				Node seriesCompletedNode = seriesStatusBaseNodeIndex.get(SERIES_STATUS, SERIES_COMPLETED_BASE_NODE).getSingle();
				
				Iterator<Relationship> completedSeriesRelsItr = seriesCompletedNode.getRelationships(SERIES_COMPLETED_REL_TO_BASE_NODE).iterator();
				LinkedList<Relationship> completedSeriesRelList = new LinkedList<Relationship>();
				LinkedList<Node> completedSeriesNodeList = new LinkedList<Node>();
				while(completedSeriesRelsItr.hasNext())
					completedSeriesRelList.addLast(completedSeriesRelsItr.next());
				
				for(Relationship rel : completedSeriesRelList)
					completedSeriesNodeList.addLast(rel.getStartNode());
				Collections.sort(completedSeriesNodeList, TrendingComparatorForSeriesNodes);				
				
				
//				Iterator<Relationship> completedSeriesRelsItr = seriesCompletedNode.getRelationships(SERIES_COMPLETED_REL_TO_BASE_NODE).iterator();
//				LinkedList<Relationship> completedSeriesRelList = new LinkedList<Relationship>();
//				while(completedSeriesRelsItr.hasNext())
//					completedSeriesRelList.addLast(completedSeriesRelsItr.next());
//				Collections.sort(completedSeriesRelList, TimeCreatedComparatorForRelationships);				
				for(Node series : completedSeriesNodeList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(!series.getProperty(SERIES_TYPE).toString().equals("2"))
						continue;
					
					Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
					Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(c < prev_cnt)
											{
												c++;
												continue;
											}
											c++;
											outputSeriesNode.addLast(series);			
											
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
									
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputSeriesNode.addLast(series);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputSeriesNode.addLast(series);			
						
					}
					
				}
			}

			else if(feedType.equalsIgnoreCase("G"))
			{
				Node genreNode = genre_index.get(GENRE_NAME, genre_name.toLowerCase()).getSingle();
				if(genreNode == null)
					throw new KahaniyaCustomException("Invalid Genre name");
				Iterator<Relationship> seriesRelItr = genreNode.getRelationships(SERIES_BELONGS_TO_GENRE).iterator();
				LinkedList<Relationship> seriesRelList = new LinkedList<Relationship>();
				while(seriesRelItr.hasNext())
					seriesRelList.addLast(seriesRelItr.next());
				Collections.sort(seriesRelList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : seriesRelList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node series = rel.getStartNode();
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						
						if(filterJSON.has("language"))
						{
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
									
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputSeriesNode.addLast(series);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputSeriesNode.addLast(series);			
						
					}
					
				}
			}

			else if(feedType.equalsIgnoreCase("LNG"))
			{
				Node langNode = lang_index.get(LANG_NAME, lang_name.toLowerCase()).getSingle();
				if(langNode == null)
					throw new KahaniyaCustomException("Invalid Language Name");
				Iterator<Relationship> seriesRelItr = langNode.getRelationships(SERIES_BELONGS_TO_LANGUAGE).iterator();
				LinkedList<Relationship> seriesRelList = new LinkedList<Relationship>();
				while(seriesRelItr.hasNext())
					seriesRelList.addLast(seriesRelItr.next());
				Collections.sort(seriesRelList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : seriesRelList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node series = rel.getStartNode();
					Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						
						if(filterJSON.has("genre"))
						{
							for(String genr: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genr.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
									
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputSeriesNode.addLast(series);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputSeriesNode.addLast(series);			
						
					}
					
				}
			}
			else if(feedType.equalsIgnoreCase("CN"))
			{
				
				if(contestId == null || contestId.length()==0)
					throw new KahaniyaCustomException("Empty parameter receieved for contest id");
				Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
				Node contestNode = contestId_index.get(CONTEST_ID, contestId).getSingle();
				if(contestNode == null)
					throw new KahaniyaCustomException("Contest does not exists with given contest id"+contestId);

				LinkedList<Relationship> contestSeriesRelsList = new LinkedList<Relationship>();
				
				if(filter != null && !filter.equals(""))
				{
					JSONObject filterJSON = new JSONObject(filter);
					if(filterJSON.has("sts"))
					{
						if("0".equals(filterJSON.getString("sts")))
						{
							for(Relationship rel: contestNode.getRelationships(SERIES_BELONGS_TO_CONTEST))
							{
								if("0".equals(rel.getProperty(SERIES_CONTEST_STATUS).toString()))
									contestSeriesRelsList.addLast(rel);
							}	
						}
						else if("1".equals(filterJSON.getString("sts")))
						{
							for(Relationship rel: contestNode.getRelationships(SERIES_BELONGS_TO_CONTEST))
							{
								if("1".equals(rel.getProperty(SERIES_CONTEST_STATUS).toString()))
									contestSeriesRelsList.addLast(rel);
							}
						}
						else if("2".equals(filterJSON.getString("sts")))
						{
							if(s_user_node == null)
								throw new KahaniyaCustomException("User does not exists with given id" + s_user_id);
							
							for(Relationship rel: contestNode.getRelationships(SERIES_BELONGS_TO_CONTEST))
							{
								if("1".equals(rel.getProperty(SERIES_CONTEST_STATUS).toString()) && !rel.hasProperty(SERIES_CONTEST_RATED_BY_JUDGES) )
									contestSeriesRelsList.addLast(rel);
								else if("1".equals(rel.getProperty(SERIES_CONTEST_STATUS).toString()) && rel.hasProperty(SERIES_CONTEST_RATED_BY_JUDGES) && !rel.getProperty(CHAPTER_CONTEST_RATED_BY_JUDGES).toString().contains(s_user_id))
									contestSeriesRelsList.addLast(rel);
							}
						}
						else
						{
							throw new KahaniyaCustomException("Invalid sts value receieved" + filterJSON.getString("sts"));
						}
					}
					else
					{
						for(Relationship rel: contestNode.getRelationships(SERIES_BELONGS_TO_CONTEST))
							contestSeriesRelsList.addLast(rel);
					}
				}
				else
				{
					for(Relationship rel: contestNode.getRelationships(SERIES_BELONGS_TO_CONTEST))
						contestSeriesRelsList.addLast(rel);
				}

				Collections.sort(contestSeriesRelsList, TimeCreatedComparatorForRelationships);
								
				for(Relationship rel : contestSeriesRelsList)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node series = rel.getStartNode();
					
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					outputSeriesNode.addLast(series);									
				}
			}


			else if(feedType.equalsIgnoreCase("D"))
			{
				ResourceIterator<Node> seriesNodesItr = seriesId_index.query(SERIES_ID, "*").iterator();
				LinkedList<Node> seriesList = new LinkedList<Node>();
				while(seriesNodesItr.hasNext())
					seriesList.addLast(seriesNodesItr.next());
				Collections.sort(seriesList, TrendingComparatorForSeriesNodes);				
				for(Node series : seriesList)
				{	
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					if(!series.getProperty(SERIES_TYPE).toString().equals("2"))
						continue;
					
					if(series.getSingleRelationship(USER_STARTED_SERIES, Direction.INCOMING).getStartNode().equals(s_user_node))
						continue;
					
					Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
					Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(c < prev_cnt)
											{
												c++;
												continue;
											}
											c++;
											outputSeriesNode.addLast(series);			
											
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
									
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputSeriesNode.addLast(series);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputSeriesNode.addLast(series);			
						
					}
					
				}
			}

			for(Node series : outputSeriesNode)
				jsonArray.put(getJSONForSeries(series, s_user_node));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_series()");
			System.out.println("Something went wrong, while returning series from get_series  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_series()");
			System.out.println("Something went wrong, while returning series from get_series  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();
	}
	
	
	private JSONArray get_user_series_feed(String feedType, String filter, int prev_cnt, int count, String s_user_id, String genre_name, String lang_name, String user_id)
	{

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> genre_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			Node s_user_node = userId_index.get(USER_ID, s_user_id).getSingle();
			
			int c = 0;
			LinkedList<Node> outputSeriesNode = new LinkedList<Node>();
			
			if(feedType.equalsIgnoreCase("W"))
			{
				if(user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				Iterator<Relationship> subscribedRelsItr = user_node.getRelationships(USER_STARTED_SERIES).iterator();
				LinkedList<Relationship> subscribedRelsList = new LinkedList<Relationship>();
				while(subscribedRelsItr.hasNext())
					subscribedRelsList.addLast(subscribedRelsItr.next());
				Collections.sort(subscribedRelsList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : subscribedRelsList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node series = rel.getEndNode();
					
					if(!series.getProperty(SERIES_TYPE).toString().equals("2"))
						continue;
					
					Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
					Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(c < prev_cnt)
											{
												c++;
												continue;
											}
											c++;
											outputSeriesNode.addLast(series);			
											
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputSeriesNode.addLast(series);			
									
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputSeriesNode.addLast(series);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputSeriesNode.addLast(series);			
						
					}
					
				}
			}
			

			
			for(Node series : outputSeriesNode)
				jsonArray.put(getJSONForSeries(series, s_user_node));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_series()");
			System.out.println("Something went wrong, while returning series from get_series  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_series()");
			System.out.println("Something went wrong, while returning series from get_series  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}

	
	private JSONArray get_nanostories(String feedType, String filter, int prev_cnt, int count, String s_user_id, String genre_name, String lang_name, String user_id, String contestId)
	{

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
//			Index<Node> nanostoryId_index = graphDb.index().forNodes(NANOSTORY_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> genre_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			Node s_user_node = userId_index.get(USER_ID, s_user_id).getSingle();
			
			int c = 0;
			LinkedList<Node> outputNanostoryNode = new LinkedList<Node>();
			
			if(feedType.equalsIgnoreCase("ALL"))
			{
				JSONObject filterJSON = new JSONObject(filter);
				String lang = filterJSON.getString("language");
				Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
//				System.out.println("Lang Name:"+lang+langNode);
				
//				if(user_node == null)
//					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				
				Iterator<Relationship> nanostoryRelsItr = langNode.getRelationships(NANOSTORY_BELONGS_TO_LANGUAGE).iterator();
				LinkedList<Relationship> nanostoryRelsList = new LinkedList<Relationship>();
				while(nanostoryRelsItr.hasNext())
					nanostoryRelsList.addLast(nanostoryRelsItr.next());
				Collections.sort(nanostoryRelsList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : nanostoryRelsList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node nanostory = rel.getStartNode();
//					System.out.println("Nanostory Id"+nanostory);
					Node nanostoryGenreNode = nanostory.getSingleRelationship(NANOSTORY_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
					Node nanostoryLangNode = nanostory.getSingleRelationship(NANOSTORY_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
					if(filter != null && !filter.equals(""))
					{
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && nanostoryGenreNode.equals(genreNode))
								{
									if(langNode != null && nanostoryLangNode.equals(langNode))
									{
										if(c < prev_cnt)
										{
											c++;												
											continue;
										}
										c++;
										outputNanostoryNode.addLast(nanostory);
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && nanostoryGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputNanostoryNode.addLast(nanostory);			
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							if(langNode != null && nanostoryLangNode.equals(langNode))
							{
								if(c < prev_cnt)
								{
									c++;
									continue;
								}
								c++;
								outputNanostoryNode.addLast(nanostory);
							}
							
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputNanostoryNode.addLast(nanostory);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputNanostoryNode.addLast(nanostory);			
						
					}
					
				}
			
			}
			else if(feedType.equalsIgnoreCase("CN"))
			{
				if(contestId == null || contestId.length()==0)
					throw new KahaniyaCustomException("Empty parameter receieved for contest id");
				Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
				Node contestNode = contestId_index.get(CONTEST_ID, contestId).getSingle();
				if(contestNode == null)
					throw new KahaniyaCustomException("Contest does not exists with given contest id"+contestId);

				LinkedList<Relationship> contestNanostoryRelsList = new LinkedList<Relationship>();
				
				if(filter != null && !filter.equals(""))
				{
					JSONObject filterJSON = new JSONObject(filter);
					if(filterJSON.has("sts"))
					{
						if("0".equals(filterJSON.getString("sts")))
						{
							for(Relationship rel: contestNode.getRelationships(NANOSTORY_BELONGS_TO_CONTEST))
							{
								if("0".equals(rel.getProperty(NANOSTORY_CONTEST_STATUS).toString()))
									contestNanostoryRelsList.addLast(rel);
							}	
						}
						else if("1".equals(filterJSON.getString("sts")))
						{
							for(Relationship rel: contestNode.getRelationships(NANOSTORY_BELONGS_TO_CONTEST))
							{
								if("1".equals(rel.getProperty(NANOSTORY_CONTEST_STATUS).toString()))
									contestNanostoryRelsList.addLast(rel);
							}
						}
						else if("2".equals(filterJSON.getString("sts")))
						{
							if(s_user_node == null)
								throw new KahaniyaCustomException("User does not exists with given id" + s_user_id);
							
							for(Relationship rel: contestNode.getRelationships(NANOSTORY_BELONGS_TO_CONTEST))
							{
								if("1".equals(rel.getProperty(NANOSTORY_CONTEST_STATUS).toString()) && !rel.hasProperty(NANOSTORY_CONTEST_RATED_BY_JUDGES) )
									contestNanostoryRelsList.addLast(rel);
								else if("1".equals(rel.getProperty(NANOSTORY_CONTEST_STATUS).toString()) && rel.hasProperty(NANOSTORY_CONTEST_RATED_BY_JUDGES) && !rel.getProperty(NANOSTORY_CONTEST_RATED_BY_JUDGES).toString().contains(s_user_id))
									contestNanostoryRelsList.addLast(rel);
							}
						}
						else
						{
							throw new KahaniyaCustomException("Invalid sts value receieved" + filterJSON.getString("sts"));
						}
					}
					else
					{
						for(Relationship rel: contestNode.getRelationships(NANOSTORY_BELONGS_TO_CONTEST))
							contestNanostoryRelsList.addLast(rel);
					}
				}
				else
				{
					for(Relationship rel: contestNode.getRelationships(NANOSTORY_BELONGS_TO_CONTEST))
						contestNanostoryRelsList.addLast(rel);
				}

				Collections.sort(contestNanostoryRelsList, TimeCreatedComparatorForRelationships);
								
				for(Relationship rel : contestNanostoryRelsList)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node nanostory = rel.getStartNode();
					
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					outputNanostoryNode.addLast(nanostory);
									
				}
			}
			else if(feedType.equalsIgnoreCase("WNS"))
			{
				if(user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				Iterator<Relationship> subscribedRelsItr = user_node.getRelationships(USER_WRITTEN_A_NANOSTORY).iterator();
				LinkedList<Relationship> subscribedRelsList = new LinkedList<Relationship>();
				while(subscribedRelsItr.hasNext())
					subscribedRelsList.addLast(subscribedRelsItr.next());
				Collections.sort(subscribedRelsList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : subscribedRelsList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node nanostory = rel.getEndNode();
					
					Node nanostoryGenreNode = nanostory.getSingleRelationship(NANOSTORY_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
					Node nanostoryLangNode = nanostory.getSingleRelationship(NANOSTORY_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && nanostoryGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && nanostoryLangNode.equals(langNode))
										{
											if(c < prev_cnt)
											{
												c++;
												continue;
											}
											c++;
											outputNanostoryNode.addLast(nanostory);			
											
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && nanostoryGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputNanostoryNode.addLast(nanostory);			
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && nanostoryLangNode.equals(langNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputNanostoryNode.addLast(nanostory);			
									
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputNanostoryNode.addLast(nanostory);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputNanostoryNode.addLast(nanostory);			
						
					}
					
				}
			}
			else if(feedType.equalsIgnoreCase("G"))
			{
				Node genreNode = genre_index.get(GENRE_NAME, genre_name.toLowerCase()).getSingle();
				if(genreNode == null)
					throw new KahaniyaCustomException("Invalid Genre name");
				Iterator<Relationship> nanostoryRelItr = genreNode.getRelationships(NANOSTORY_BELONGS_TO_GENRE).iterator();
				LinkedList<Relationship> nanostoryRelList = new LinkedList<Relationship>();
				while(nanostoryRelItr.hasNext())
					nanostoryRelList.addLast(nanostoryRelItr.next());
				Collections.sort(nanostoryRelList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : nanostoryRelList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node nanostory = rel.getStartNode();
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						
						if(filterJSON.has("language"))
						{
							Node nanostoryLangNode = nanostory.getSingleRelationship(NANOSTORY_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && nanostoryLangNode.equals(langNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputNanostoryNode.addLast(nanostory);			
									
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputNanostoryNode.addLast(nanostory);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputNanostoryNode.addLast(nanostory);			
						
					}
					
				}
			}

			else if(feedType.equalsIgnoreCase("LNG"))
			{
				Node langNode = lang_index.get(LANG_NAME, lang_name.toLowerCase()).getSingle();
				if(langNode == null)
					throw new KahaniyaCustomException("Invalid Language Name");
				Iterator<Relationship> nanostoryRelItr = langNode.getRelationships(NANOSTORY_BELONGS_TO_LANGUAGE).iterator();
				LinkedList<Relationship> nanostoryRelList = new LinkedList<Relationship>();
				while(nanostoryRelItr.hasNext())
					nanostoryRelList.addLast(nanostoryRelItr.next());
				Collections.sort(nanostoryRelList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : nanostoryRelList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node nanostory = rel.getStartNode();
					Node nanostoryGenreNode = nanostory.getSingleRelationship(NANOSTORY_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						
						if(filterJSON.has("genre"))
						{
							for(String genr: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genr.toLowerCase()).getSingle();
								if(genreNode != null && nanostoryGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputNanostoryNode.addLast(nanostory);			
									
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputNanostoryNode.addLast(nanostory);			
							
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputNanostoryNode.addLast(nanostory);			
						
					}
					
				}
			}

			
			for(Node nanostory : outputNanostoryNode)
				jsonArray.put(getJSONForNanostory(nanostory, s_user_node));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_nanostories()");
			System.out.println("Something went wrong, while returning series from get_nanostories  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_series()");
			System.out.println("Something went wrong, while returning series from get_nanostories  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}
	
	private JSONArray get_user_anthologies_feed(String feedType, String filter, int prev_cnt, int count, String s_user_id, String genre_name, String lang_name, String user_id)
	{

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
//			Index<Node> anthologyId_index = graphDb.index().forNodes(ANTHOLOGY_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			Node s_user_node = userId_index.get(USER_ID, s_user_id).getSingle();
			
			int c = 0;
			LinkedList<Node> outputAnthologyNode = new LinkedList<Node>();
			
			if(feedType.equalsIgnoreCase("L"))
			{
				
				JSONObject filterJSON = new JSONObject(filter);
				Node langNode = lang_index.get(LANG_NAME, filterJSON.getString("language").toLowerCase()).getSingle();
				if(langNode == null)
					throw new KahaniyaCustomException("Invalid Language Name");
				Iterator<Relationship> anthologyRelItr = langNode.getRelationships(ANTHOLOGY_BELONGS_TO_LANGUAGE).iterator();
				LinkedList<Relationship> anthologyRelList = new LinkedList<Relationship>();
				while(anthologyRelItr.hasNext())
					anthologyRelList.addLast(anthologyRelItr.next());
				Collections.sort(anthologyRelList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : anthologyRelList)
				{								
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node anthology = rel.getStartNode();
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					outputAnthologyNode.addLast(anthology);			
				}
			}
			else 
			{
				
				Iterator<Relationship> anthologyRelItr = user_node.getRelationships(USER_STARTED_ANTHOLOGY).iterator();
				LinkedList<Relationship> anthologyRelList = new LinkedList<Relationship>();
				while(anthologyRelItr.hasNext())
					anthologyRelList.addLast(anthologyRelItr.next());
				Collections.sort(anthologyRelList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : anthologyRelList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node anthology = rel.getEndNode();
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					outputAnthologyNode.addLast(anthology);			
				}
			}

			
			for(Node anthology : outputAnthologyNode)
				jsonArray.put(getJSONForAnthologies(anthology, s_user_node));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_anthologies()");
			System.out.println("Something went wrong, while returning anthologies from get_anthologies  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_anthologies()");
			System.out.println("Something went wrong, while returning anthologies from get_anthologies  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}

	private String get_recent_purchases(String feedType, String filter, int prev_cnt, int count, String s_user_id)
	{

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
//			Index<Node> anthologyId_index = graphDb.index().forNodes(ANTHOLOGY_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);		
			Node s_user_node;
			if(s_user_id == null)
				s_user_node = null;
			else
				s_user_node = userId_index.get(USER_ID, s_user_id).getSingle();
			
			int c = 0;
			LinkedList<Node> outputNodes = new LinkedList<Node>();
			
			if(feedType.equalsIgnoreCase("C"))
			{
				
				JSONObject filterJSON = new JSONObject(filter);
				
				Index<Relationship> userPurchasedChapterRelIndex = graphDb.index().forRelationships(USER_PURCHASED_CHAPTER_REL_INDEX);
				IndexHits<Relationship> hits = userPurchasedChapterRelIndex.query(new QueryContext(filterJSON.getString("language").toLowerCase()));
				LinkedList<Relationship> filteredRels = new LinkedList<Relationship>();
				
				for (Relationship r : hits){
					filteredRels.add(r);
				}
				
				Collections.sort(filteredRels, TimeCreatedComparatorForRelationships);
															
				for(Relationship rel : filteredRels)
				{	
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node chapter = rel.getEndNode();
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					outputNodes.addLast(chapter);			
				}
				
				for(Node chapter : outputNodes)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
			}else if(feedType.equalsIgnoreCase("S")){
				JSONObject filterJSON = new JSONObject(filter);
				
				Index<Relationship> userPurchasedSeriesRelIndex = graphDb.index().forRelationships(USER_PURCHASED_SERIES_REL_INDEX);
				IndexHits<Relationship> hits = userPurchasedSeriesRelIndex.query(new QueryContext(filterJSON.getString("language").toLowerCase()));
				LinkedList<Relationship> filteredRels = new LinkedList<Relationship>();
				
				for (Relationship r : hits){
					filteredRels.add(r);
				}
				
				Collections.sort(filteredRels, TimeCreatedComparatorForRelationships);
															
				for(Relationship rel : filteredRels)
				{	
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node series = rel.getEndNode();
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					outputNodes.addLast(series);			
				}
				for(Node series : outputNodes)
					jsonArray.put(getJSONForChapter(series, s_user_node));
			}
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_recent_purchases()");
			System.out.println("Something went wrong, while returning chapters/serises from get_recent_purchases  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_recent_purchases()");
			System.out.println("Something went wrong, while returning chapters/serieses from get_recent_purchases  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();
	}

	
	private String get_anthologies(String feedType, String filter, int prev_cnt, int count, String s_user_id, String genre_name, String lang_name, String user_id)
	{

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
//			Index<Node> anthologyId_index = graphDb.index().forNodes(ANTHOLOGY_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			Node s_user_node = userId_index.get(USER_ID, s_user_id).getSingle();
			
			int c = 0;
			LinkedList<Node> outputAnthologyNode = new LinkedList<Node>();
			
			if(feedType.equalsIgnoreCase("L"))
			{
				
				JSONObject filterJSON = new JSONObject(filter);
				Node langNode = lang_index.get(LANG_NAME, filterJSON.getString("language").toLowerCase()).getSingle();
				if(langNode == null)
					throw new KahaniyaCustomException("Invalid Language Name");
				Iterator<Relationship> anthologyRelItr = langNode.getRelationships(ANTHOLOGY_BELONGS_TO_LANGUAGE).iterator();
				LinkedList<Relationship> anthologyRelList = new LinkedList<Relationship>();
				while(anthologyRelItr.hasNext())
					anthologyRelList.addLast(anthologyRelItr.next());
				Collections.sort(anthologyRelList, TimeCreatedComparatorForRelationships);				
				for(Relationship rel : anthologyRelList)
				{				
					
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node anthology = rel.getStartNode();
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					outputAnthologyNode.addLast(anthology);			
				}
			}
			
			for(Node anthology : outputAnthologyNode)
				jsonArray.put(getJSONForAnthologies(anthology, s_user_node));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_anthologies()");
			System.out.println("Something went wrong, while returning anthologies from get_anthologies  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_anthologies()");
			System.out.println("Something went wrong, while returning anthologies from get_anthologies  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();
	}
	
	private JSONArray get_popular_authors(String filter, int prev_cnt, int count)
	{
		JSONArray jsonArray = new JSONArray();
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
								
			int c = 0;
			LinkedList<Node> outputUserNode = new LinkedList<Node>();
			ResourceIterator<Node> seriesNodesItr = seriesId_index.query(SERIES_ID, "*").iterator();
			LinkedList<Node> seriesList = new LinkedList<Node>();
			while(seriesNodesItr.hasNext())
				seriesList.addLast(seriesNodesItr.next());
			
			LinkedList<Node> chaptersList = new LinkedList<Node>();
			LinkedList<Node> authorsList = new LinkedList<Node>();
			
			JSONObject filterJSON = new JSONObject(filter);
			Node langNode = lang_index.get(LANG_NAME, filterJSON.getString("language").toLowerCase()).getSingle();
			
			Iterator<Relationship> pinnedAuthors = langNode.getRelationships(USER_PINNED_TO_LANGUAGE).iterator();
			
			LinkedList<Relationship> list = new LinkedList<Relationship>();
			while(pinnedAuthors.hasNext())
			{
				Relationship t_rel = pinnedAuthors.next();
				list.addLast(t_rel);
			}
			Collections.sort(list, TimeCreatedComparatorForRelationships);
			
			for(Relationship rel : list)
			{
				Node author = rel.getStartNode();
				
				if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
					break;
				if(c < prev_cnt)
				{
					c++;
					continue;
				}
				else
				{
					c++;
					outputUserNode.addLast(author);			
				}
			}
						
			for(Node series : seriesList)
			{
				Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
				if(filter != null && !filter.equals("")){
					
										
					if(filterJSON.has("language"))
					{	
						if(langNode != null && seriesLangNode.equals(langNode))
						{
							Iterator<Relationship> seriesChapters = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
							while(seriesChapters.hasNext())
								chaptersList.addLast(seriesChapters.next().getStartNode());
						}
					}
					else{
						Iterator<Relationship> seriesChapters = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
						while(seriesChapters.hasNext())
							chaptersList.addLast(seriesChapters.next().getStartNode());
					}
				}
				else{
					Iterator<Relationship> seriesChapters = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
					while(seriesChapters.hasNext())
						chaptersList.addLast(seriesChapters.next().getStartNode());
					}
			}
			for(Node chapter : chaptersList)
			{
				Node auth = chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
				if(auth.hasProperty(IS_DELETED) && auth.getProperty(IS_DELETED).toString().equals("1"))
					continue;
//				if(!authorsList.contains(auth) && Integer.parseInt(auth.getProperty(STATUS).toString()) == 2)
				if(!authorsList.contains(auth))
					authorsList.add(auth);
			}
			
			Collections.sort(authorsList, DiscoverComparatorForUserNodes);
			for(Node user : authorsList)
			{
				if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
					break;
				if(c < prev_cnt)
				{
					c++;
					continue;
				}
				else
				{
					c++;
					outputUserNode.addLast(user);			
				}
			}
			for(Node user : outputUserNode)
				jsonArray.put(getJSONForUser(user,null));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_popular_authors()");
			System.out.println("Something went wrong, while returning authors from get_popular_authors  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_popular_authors()");
			System.out.println("Something went wrong, while returning authors from get_popular_authors  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		
		return jsonArray;
	}

	
	private String get_authors(String feedType, String filter, int prev_cnt, int count, String user_id)
	{
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> genre_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			if(user_id == null)
				user_id = "";
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			
			int c = 0;
			LinkedList<Node> outputUserNode = new LinkedList<Node>();

			if(feedType.equalsIgnoreCase("D"))
			{

				ResourceIterator<Node> seriesNodesItr = seriesId_index.query(SERIES_ID, "*").iterator();
				LinkedList<Node> seriesList = new LinkedList<Node>();
				while(seriesNodesItr.hasNext())
					seriesList.addLast(seriesNodesItr.next());
				
				LinkedList<Node> chaptersList = new LinkedList<Node>();
				LinkedList<Node> authorsList = new LinkedList<Node>();
				
				for(Node series : seriesList)
				{
					Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
					Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
					
					if(filter != null && !filter.equals("")){
						JSONObject filterJSON = new JSONObject(filter);
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											Iterator<Relationship> seriesChapters = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
											while(seriesChapters.hasNext())
												chaptersList.addLast(seriesChapters.next().getStartNode());
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									Iterator<Relationship> seriesChapters = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
									while(seriesChapters.hasNext())
										chaptersList.addLast(seriesChapters.next().getStartNode());
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									Iterator<Relationship> seriesChapters = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
									while(seriesChapters.hasNext())
										chaptersList.addLast(seriesChapters.next().getStartNode());
								}
							}
						}
						else{
							Iterator<Relationship> seriesChapters = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
							while(seriesChapters.hasNext())
								chaptersList.addLast(seriesChapters.next().getStartNode());
						}
					}
					else{
						Iterator<Relationship> seriesChapters = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
						while(seriesChapters.hasNext())
							chaptersList.addLast(seriesChapters.next().getStartNode());
					}
				}

				for(Node chapter : chaptersList)
				{			
					Node auth = chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
					if(auth.equals(user_node))
						continue;
					if(auth.hasProperty(IS_DELETED) && auth.getProperty(IS_DELETED).toString().equals("1"))
						continue;
					if(isRelationExistsBetween(USER_FOLLOW_USER, user_node, auth))
						continue;
					if(!authorsList.contains(auth))
						authorsList.add(auth);
										
				}
				
				Collections.sort(authorsList, DiscoverComparatorForUserNodes);
				
				for(Node user : authorsList)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;

					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					else
					{
						c++;
						outputUserNode.addLast(user);			
					}
				}
			}

			for(Node user : outputUserNode)
				jsonArray.put(getJSONForUser(user, user_node));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_authors()");
			System.out.println("Something went wrong, while returning authors from get_authors  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_authors()");
			System.out.println("Something went wrong, while returning authors from get_authors  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}	
		return jsonArray.toString();
	}
	
	private String get_contests(String feedType, String filter, int prev_cnt, int count, String user_id)
	{
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			if(user_id == null)
				user_id = "";
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();
			
			int c = 0;

			if(feedType.equalsIgnoreCase("L"))
			{

				ResourceIterator<Node> contestNodesItr = contestId_index.query(CONTEST_ID, "*").iterator();
				LinkedList<Node> contestsList = new LinkedList<Node>();
				LinkedList<Node> outputList = new LinkedList<Node>();
				while(contestNodesItr.hasNext())
					contestsList.addLast(contestNodesItr.next());
				
				int presentTime = (int)(System.currentTimeMillis()/1000);
				int listType = 1;
				
				JSONObject filterJSON;
				if(filter != null && !filter.equals(""))
					filterJSON = new JSONObject(filter);	
				else filterJSON = new JSONObject();
				
				if(filterJSON.has("actv") && "0".equalsIgnoreCase(filterJSON.getString("actv")))
					listType = 0;
				
				for(Node contest : contestsList)
				{			
					if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(contest.getProperty(CONTEST_PRICE).toString()))
						continue;
					if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(contest.getProperty(CONTEST_PRICE).toString()))
						continue;
					Node auth = contest.getSingleRelationship(USER_STARTED_CONTEST, Direction.INCOMING).getStartNode();

					if(auth.hasProperty(IS_DELETED) && auth.getProperty(IS_DELETED).toString().equals("1"))
						continue;

					if(filterJSON.has("language"))
					{
						for(String lang: filterJSON.getString("language").split(","))
						{
							if(lang.equalsIgnoreCase(contest.getProperty(CONTEST_LANGUAGE).toString()))
							{
								if(listType == 1 && Integer.parseInt(contest.getProperty(CONTEST_END_DATE).toString()) > presentTime)
									outputList.addLast(contest);
								else if(listType == 0 && Integer.parseInt(contest.getProperty(CONTEST_END_DATE).toString()) <= presentTime)
									outputList.addLast(contest);
							}
						}
					}
					else{
						if(listType == 1 && Integer.parseInt(contest.getProperty(CONTEST_END_DATE).toString()) > presentTime)
							outputList.addLast(contest);
						else if(listType == 0 && Integer.parseInt(contest.getProperty(CONTEST_END_DATE).toString()) <= presentTime)
							outputList.addLast(contest);
					}
				}
				if(listType == 1)
					Collections.sort(outputList, TimeCreatedComparatorForNodes);
				else
					Collections.sort(outputList, EndTimeComparatorForContestNodes);
				
				for(Node contest : outputList)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;

					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					else
					{
						c++;
						jsonArray.put(getJSONForContest(contest, user_node));			
					}
				}
			}				
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_contests()");
			System.out.println("Something went wrong, while returning authors from get_contests  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_contests()");
			System.out.println("Something went wrong, while returning contests from get_contests  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();
	}
	
	private JSONArray get_user_chapters_feed(String feedType, String filter, int prev_cnt, int count, String s_user_id, String genre_name, String lang_name, String user_id, String contestId)
	{

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> genre_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			Node s_user_node = userId_index.get(USER_ID, s_user_id).getSingle();
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();

			LinkedList<Node> outputChaptersNode = new LinkedList<Node>();

			if(feedType.equalsIgnoreCase("W"))
			{
				int i = 0;
				if(user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				Iterator<Relationship> favCHaptersRelsItr = user_node.getRelationships(USER_WRITTEN_A_CHAPTER).iterator();
				LinkedList<Relationship> favChaptersRelsList = new LinkedList<Relationship>();
				while(favCHaptersRelsItr.hasNext())
					favChaptersRelsList.addLast(favCHaptersRelsItr.next());
				Collections.sort(favChaptersRelsList, TimeCreatedComparatorForRelationships);
								
				for(Relationship rel : favChaptersRelsList)
				{
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node chapter = rel.getEndNode();
					
					if(Integer.parseInt((chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode()).getProperty(SERIES_TYPE).toString()) == 1)
					{					
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);	
						
						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(i < prev_cnt)
											{
												i++;
												continue;
											}
											
											i++;
											outputChaptersNode.addLast(chapter);
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
							
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
						
						i++;
						outputChaptersNode.addLast(chapter);
					}
					}
					
				}
			}

			for(Node chapter : outputChaptersNode)
				jsonArray.put(getJSONForChapter(chapter, s_user_node));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}
	
	private String get_chapters(String feedType, String filter, int prev_cnt, int count, String s_user_id, String genre_name, String lang_name, String user_id, String contestId)
	{

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> genre_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			
			Node s_user_node = userId_index.get(USER_ID, s_user_id).getSingle();
			Node user_node = userId_index.get(USER_ID, user_id).getSingle();

			LinkedList<Node> outputChaptersNode = new LinkedList<Node>();

			if(feedType.equalsIgnoreCase("F"))
			{
				int c = 0;
				
				if(s_user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+s_user_id);
				Iterator<Relationship> favCHaptersRelsItr = s_user_node.getRelationships(USER_FAV_CHAPTER).iterator();
				LinkedList<Relationship> favChaptersRelsList = new LinkedList<Relationship>();
				while(favCHaptersRelsItr.hasNext())
					favChaptersRelsList.addLast(favCHaptersRelsItr.next());
				Collections.sort(favChaptersRelsList, TimeCreatedComparatorForRelationships);
								
				for(Relationship rel : favChaptersRelsList)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node chapter = rel.getEndNode();
					
					if(filter != null && !filter.equals(""))
					{
						
						JSONObject filterJSON = new JSONObject(filter);	
						
						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(c < prev_cnt)
											{
												c++;
												continue;
											}
											c++;
											outputChaptersNode.addLast(chapter);
	
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputChaptersNode.addLast(chapter);
					}
					
				}
				
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}
			else if(feedType.equalsIgnoreCase("CN"))
			{
				int c = 0;
				if(contestId == null || contestId.length()==0)
					throw new KahaniyaCustomException("Empty parameter receieved for contest id");
				
				System.out.println("Chapters of Contest");
				
				Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
				Node contestNode = contestId_index.get(CONTEST_ID, contestId).getSingle();
				if(contestNode == null)
					throw new KahaniyaCustomException("Contest does not exists with given contest id"+contestId);

				LinkedList<Relationship> contestChaptersRelsList = new LinkedList<Relationship>();
				System.out.println("COntest Visibility ----- "+Integer.parseInt(contestNode.getProperty(CONTEST_VISIBILTY).toString()));
				if(Integer.parseInt(contestNode.getProperty(CONTEST_VISIBILTY).toString())==0){
					System.out.println("Closed COntest----- "+Integer.parseInt(contestNode.getProperty(CONTEST_VISIBILTY).toString()));
					for(Relationship rel: contestNode.getRelationships(CHAPTER_BELONGS_TO_CONTEST))
					{
						System.out.println("EndNode"+rel.getStartNode().getProperty(CHAPTER_TITLE));
						
											
						if(isRelationExistsBetween(USER_WRITTEN_A_CHAPTER_TO_CLOSED_CONTEST, s_user_node, rel.getStartNode())){
							System.out.println("Closed Contest inside");
							contestChaptersRelsList.addLast(rel);
						}
							
					}
					
					Collections.sort(contestChaptersRelsList, TimeCreatedComparatorForRelationships);
					
					for(Relationship rel : contestChaptersRelsList)
					{
						if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
							break;
						
						Node chapter = rel.getStartNode();
						
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputChaptersNode.addLast(chapter);
										
					}
					
					for(Node chapter : outputChaptersNode){
						jsonArray.put(getJSONForChapterClosed(chapter, s_user_node));
					}
					
					tx.success();
					
					
				}else{
					System.out.println("Open Contest");
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);
						if(filterJSON.has("sts"))
						{
							if("0".equals(filterJSON.getString("sts")))
							{
								for(Relationship rel: contestNode.getRelationships(CHAPTER_BELONGS_TO_CONTEST))
								{
									if("0".equals(rel.getProperty(CHAPTER_CONTEST_STATUS).toString()))
										contestChaptersRelsList.addLast(rel);
								}	
							}
							else if("1".equals(filterJSON.getString("sts")))
							{
								for(Relationship rel: contestNode.getRelationships(CHAPTER_BELONGS_TO_CONTEST))
								{
									if("1".equals(rel.getProperty(CHAPTER_CONTEST_STATUS).toString()))
										contestChaptersRelsList.addLast(rel);
								}
							}
							else if("2".equals(filterJSON.getString("sts")))
							{
								if(s_user_node == null)
									throw new KahaniyaCustomException("User does not exists with given id" + s_user_id);
								
								for(Relationship rel: contestNode.getRelationships(CHAPTER_BELONGS_TO_CONTEST))
								{
									if("1".equals(rel.getProperty(CHAPTER_CONTEST_STATUS).toString()) && !rel.hasProperty(CHAPTER_CONTEST_RATED_BY_JUDGES) )
										contestChaptersRelsList.addLast(rel);
									else if("1".equals(rel.getProperty(CHAPTER_CONTEST_STATUS).toString()) && rel.hasProperty(CHAPTER_CONTEST_RATED_BY_JUDGES) && !rel.getProperty(CHAPTER_CONTEST_RATED_BY_JUDGES).toString().contains(s_user_id))
										contestChaptersRelsList.addLast(rel);
								}
							}
							else
							{
								throw new KahaniyaCustomException("Invalid sts value receieved" + filterJSON.getString("sts"));
							}
						}
						else
						{
							for(Relationship rel: contestNode.getRelationships(CHAPTER_BELONGS_TO_CONTEST))
								contestChaptersRelsList.addLast(rel);
						}
					}
					else
					{
						for(Relationship rel: contestNode.getRelationships(CHAPTER_BELONGS_TO_CONTEST))
							contestChaptersRelsList.addLast(rel);
					}
				
				Collections.sort(contestChaptersRelsList, TimeCreatedComparatorForRelationships);
								
				for(Relationship rel : contestChaptersRelsList)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node chapter = rel.getStartNode();
					
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					outputChaptersNode.addLast(chapter);
									
				}
				
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
				
			 }
			}
			else if(feedType.equalsIgnoreCase("AN"))
			{
				int c = 0;
				if(contestId == null || contestId.length()==0)
					throw new KahaniyaCustomException("Empty parameter receieved for anthology id");
				Index<Node> anthologyId_index = graphDb.index().forNodes(ANTHOLOGY_ID_INDEX);
				Node anthologyNode = anthologyId_index.get(ANTHOLOGY_ID, contestId).getSingle();
				if(anthologyNode == null)
					throw new KahaniyaCustomException("Anthology does not exists with given anthology id"+contestId);

				LinkedList<Relationship> anthologyChaptersRelsList = new LinkedList<Relationship>();
				
				for(Relationship rel: anthologyNode.getRelationships(ANTHOLOGY_TAGGED_A_POST))
					anthologyChaptersRelsList.addLast(rel);
				

				Collections.sort(anthologyChaptersRelsList, TimeCreatedComparatorForRelationships);
								
				for(Relationship rel : anthologyChaptersRelsList)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node chapter = rel.getEndNode();
					
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					outputChaptersNode.addLast(chapter);
									
				}
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}

			else if(feedType.equalsIgnoreCase("SRS"))
			{
				int c = 0;
				if(contestId == null || contestId.length()==0)
					throw new KahaniyaCustomException("Empty parameter receieved for contest/series id");
				Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
				Node seriesNode = seriesId_index.get(SERIES_ID, contestId).getSingle();
				if(seriesNode == null)
					throw new KahaniyaCustomException("Series does not exists with given series id"+contestId);

				LinkedList<Relationship> seriesChaptersRelsList = new LinkedList<Relationship>();
				
				if(filter != null && !filter.equals(""))
				{
					
					JSONObject filterJSON = new JSONObject(filter);	

					for(Relationship rel: seriesNode.getRelationships(CHAPTER_BELONGS_TO_SERIES))
					{
						Node chapter = rel.getEndNode();
							
						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						
						seriesChaptersRelsList.addLast(rel);	
					}
				}
				else
				{
					for(Relationship rel: seriesNode.getRelationships(CHAPTER_BELONGS_TO_SERIES))
						seriesChaptersRelsList.addLast(rel);
				}

				Collections.sort(seriesChaptersRelsList, TimeCreatedComparatorForRelationships);
				Collections.reverse(seriesChaptersRelsList);
				for(Relationship rel : seriesChaptersRelsList)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node chapter = rel.getStartNode();
					
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					c++;
					outputChaptersNode.addLast(chapter);
									
				}
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}
			else if(feedType.equalsIgnoreCase("B"))
			{
				int c = 0;
				
				if(s_user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+s_user_id);
				Iterator<Relationship> favCHaptersRelsItr = s_user_node.getRelationships(USER_BOOKMARK_CHAPTER).iterator();
				LinkedList<Relationship> favChaptersRelsList = new LinkedList<Relationship>();
				while(favCHaptersRelsItr.hasNext())
					favChaptersRelsList.addLast(favCHaptersRelsItr.next());
				Collections.sort(favChaptersRelsList, TimeCreatedComparatorForRelationships);
								
				for(Relationship rel : favChaptersRelsList)
				{
					if(c >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node chapter = rel.getEndNode();
					
					if(filter != null && !filter.equals(""))
					{
						
						JSONObject filterJSON = new JSONObject(filter);	
						
						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(c < prev_cnt)
											{
												c++;
												continue;
											}
											c++;
											outputChaptersNode.addLast(chapter);
	
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(c < prev_cnt)
									{
										c++;
										continue;
									}
									c++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(c < prev_cnt)
							{
								c++;
								continue;
							}
							c++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(c < prev_cnt)
						{
							c++;
							continue;
						}
						c++;
						outputChaptersNode.addLast(chapter);
					}
					
				}
				
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}
			
			else if(feedType.equalsIgnoreCase("T"))
			{
				Index<Node> chapter_id_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
				ResourceIterator<Node> allChapters = chapter_id_index.query(CHAPTER_ID, "*").iterator();
				LinkedList<Node> allChaptersList = new LinkedList<Node>();
				while(allChapters.hasNext())
					allChaptersList.addLast(allChapters.next());
				Collections.sort(allChaptersList, TrendingComparatorForChapterNodes);
				
//				LinkedList<Node> authors = new LinkedList<Node>();

				
				int i = 0;
				for(Node chapter : allChaptersList)
				{
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);	

						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{

											if(i < prev_cnt)
											{
												i++;
												continue;
											}
//Suggestion given by SM
/*											Node auth = chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
											if(authors.contains(auth))
												continue;
											authors.addLast(auth);
*/
											i++;
											outputChaptersNode.addLast(chapter);
	
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
//Suggestion given by SM									
/*									Node auth = chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
									if(authors.contains(auth))
										continue;
									authors.addLast(auth);
*/
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
//Suggestion given by SM
/*									Node auth = chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
									if(authors.contains(auth))
										continue;
									authors.addLast(auth);
*/
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
//Suggestion given by SM							
/*							Node auth = chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
							if(authors.contains(auth))
								continue;
							authors.addLast(auth);
*/
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
//Suggestion given by SM						
/*						Node auth = chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
						if(authors.contains(auth))
							continue;
						authors.addLast(auth);
*/
						i++;
						outputChaptersNode.addLast(chapter);
					}
					
				}
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}

			
			else if(feedType.equalsIgnoreCase("G"))
			{
				Node genreNode = genre_index.get(GENRE_NAME, genre_name.toLowerCase()).getSingle();
				if(genreNode == null)
					throw new KahaniyaCustomException("Invalid Genre name");
				Iterator<Relationship> seriesRelItr = genreNode.getRelationships(SERIES_BELONGS_TO_GENRE).iterator();
				LinkedList<Node> chaptersList = new LinkedList<Node>();
				while(seriesRelItr.hasNext())
				{
					Node t = seriesRelItr.next().getStartNode();
					Iterator<Relationship> chaptersRelItr = t.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
					while(chaptersRelItr.hasNext())
						chaptersList.addLast(chaptersRelItr.next().getStartNode());
				}
				Collections.sort(chaptersList, TimeCreatedComparatorForNodes);				
				int i = 0;
				for(Node chapter : chaptersList)
				{
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);	

						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;

						if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
							
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
						
						i++;
						outputChaptersNode.addLast(chapter);
					}
					
				}
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}

			
			else if(feedType.equalsIgnoreCase("LNG"))
			{
				Node langNode = lang_index.get(LANG_NAME, lang_name.toLowerCase()).getSingle();
				if(langNode == null)
					throw new KahaniyaCustomException("Invalid Language name");
				Iterator<Relationship> seriesRelItr = langNode.getRelationships(SERIES_BELONGS_TO_LANGUAGE).iterator();
				LinkedList<Node> chaptersList = new LinkedList<Node>();
				while(seriesRelItr.hasNext())
				{
					Node t = seriesRelItr.next().getStartNode();
					Iterator<Relationship> chaptersRelItr = t.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
					while(chaptersRelItr.hasNext())
						chaptersList.addLast(chaptersRelItr.next().getStartNode());
				}
				Collections.sort(chaptersList, TimeCreatedComparatorForNodes);				
				int i = 0;
				for(Node chapter : chaptersList)
				{
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);	
						
						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						
						if(filterJSON.has("genre"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(langNode != null && seriesGenreNode.equals(genreNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
							
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
						
						i++;
						outputChaptersNode.addLast(chapter);
					}
					
				}
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}
			
			else if(feedType.equalsIgnoreCase("D"))
			{

				Index<Node> chapter_id_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
				ResourceIterator<Node> allChapters = chapter_id_index.query(CHAPTER_ID, "*").iterator();
				LinkedList<Node> allChaptersList = new LinkedList<Node>();
				while(allChapters.hasNext())
					allChaptersList.addLast(allChapters.next());
				Collections.sort(allChaptersList, TrendingComparatorForChapterNodes);
					
				int i = 0;
				for(Node chapter : allChaptersList)
				{
					if(chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode().equals(s_user_node))
						continue;
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);	
						
						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{

							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(i < prev_cnt)
											{
												i++;
												continue;
											}
											
											i++;
											outputChaptersNode.addLast(chapter);
	
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{

							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else if(filterJSON.has("language"))
						{

							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
							
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
						
						i++;
						outputChaptersNode.addLast(chapter);
					}
					
				}
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}
			
			else if(feedType.equalsIgnoreCase("L"))
			{
				LinkedList<Node> allChaptersList = new LinkedList<Node>();
				if(s_user_node == null)
				{
					//throw new KahaniyaCustomException("No user found for the parameter session_user_id");
					Index<Node> chapter_id_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
					ResourceIterator<Node> allChapters = chapter_id_index.query(CHAPTER_ID, "*").iterator();
					
					while(allChapters.hasNext())
						allChaptersList.addLast(allChapters.next());
				}
				else
				{
					Iterator<Relationship> followingLangRels = s_user_node.getRelationships(USER_INTERESTED_LANGUAGE).iterator();
					LinkedList<Node> followingLangs = new LinkedList<Node>();
					while(followingLangRels.hasNext())
						followingLangs.addLast(followingLangRels.next().getEndNode());				
									
					for(Node lang : followingLangs)
					{
						Iterator<Relationship> followingLangSeriesItr = lang.getRelationships(SERIES_BELONGS_TO_LANGUAGE).iterator();
						while(followingLangSeriesItr.hasNext())
						{
							Node series = followingLangSeriesItr.next().getStartNode();
							Iterator<Relationship> chaptersItr = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
							while(chaptersItr.hasNext())
								allChaptersList.addLast(chaptersItr.next().getStartNode());
						}
					}
				}
				
				// removed - shuffeling for few days rather sorting based on created time
				//Collections.shuffle(allChaptersList);
				
				Collections.sort(allChaptersList, TimeCreatedComparatorForNodes);
				int i = 0;
				for(Node chapter : allChaptersList)
				{
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);	
						
						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(i < prev_cnt)
											{
												i++;
												continue;
											}
											
											i++;
											outputChaptersNode.addLast(chapter);
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
							
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
						
						i++;
						outputChaptersNode.addLast(chapter);
					}
					
				}
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}

			else if(feedType.equalsIgnoreCase("H"))
			{
				int i = 0;
				if(s_user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+s_user_id);
				Iterator<Relationship> favCHaptersRelsItr = s_user_node.getRelationships(USER_VIEWED_A_CHAPTER).iterator();
				LinkedList<Relationship> favChaptersRelsList = new LinkedList<Relationship>();
				while(favCHaptersRelsItr.hasNext())
					favChaptersRelsList.addLast(favCHaptersRelsItr.next());
				Collections.sort(favChaptersRelsList, TimeCreatedComparatorForRelationships);
								
				for(Relationship rel : favChaptersRelsList)
				{
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node chapter = rel.getEndNode();
					
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);	
						
						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(i < prev_cnt)
											{
												i++;
												continue;
											}
											
											i++;
											outputChaptersNode.addLast(chapter);
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
							
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
						
						i++;
						outputChaptersNode.addLast(chapter);
					}
					
				}
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}
			
			else if(feedType.equalsIgnoreCase("P"))
			{
				int i = 0;
				if(s_user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+s_user_id);
				
				Iterator<Relationship> purchasedCHaptersRelsItr = s_user_node.getRelationships(USER_PURCHASED_A_CHAPTER).iterator();
				LinkedList<Relationship> purchasedChaptersRelsList = new LinkedList<Relationship>();
				while(purchasedCHaptersRelsItr.hasNext())
					purchasedChaptersRelsList.addLast(purchasedCHaptersRelsItr.next());
				Collections.sort(purchasedChaptersRelsList, TimeCreatedComparatorForRelationships);
								
				for(Relationship rel : purchasedChaptersRelsList)
				{
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					//for every chapter view, there will a purchase with 0 or more coins, so skip 0 coins purchases
					if(Integer.parseInt(rel.getProperty(CHAPTER_PURCHASED_PRICE).toString()) <= 0)
						continue;
					
					Node chapter = rel.getEndNode();
					
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);	
																
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(i < prev_cnt)
											{
												i++;
												continue;
											}
											
											i++;
											outputChaptersNode.addLast(chapter);
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
							
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
						
						i++;
						outputChaptersNode.addLast(chapter);
					}
					
				}
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}

			else if(feedType.equalsIgnoreCase("W"))
			{
				int i = 0;
				if(user_node == null)
					throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
				Iterator<Relationship> favCHaptersRelsItr = user_node.getRelationships(USER_WRITTEN_A_CHAPTER).iterator();
				LinkedList<Relationship> favChaptersRelsList = new LinkedList<Relationship>();
				while(favCHaptersRelsItr.hasNext())
					favChaptersRelsList.addLast(favCHaptersRelsItr.next());
				Collections.sort(favChaptersRelsList, TimeCreatedComparatorForRelationships);
								
				for(Relationship rel : favChaptersRelsList)
				{
					if(i >= prev_cnt + count) // break the loop, if we got enough / required nodes to return
						break;
					
					Node chapter = rel.getEndNode();
					
					if(filter != null && !filter.equals(""))
					{
						JSONObject filterJSON = new JSONObject(filter);	
						
						if(filterJSON.has("price") && filterJSON.getString("price").equals("0") && !"0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						if(filterJSON.has("price") && filterJSON.getString("price").equals("1") && "0".equals(chapter.getProperty(CHAPTER_FREE_OR_PAID).toString()))
							continue;
						
						if(filterJSON.has("genre") && filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									for(String lang: filterJSON.getString("language").split(","))
									{
										Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
										if(langNode != null && seriesLangNode.equals(langNode))
										{
											if(i < prev_cnt)
											{
												i++;
												continue;
											}
											
											i++;
											outputChaptersNode.addLast(chapter);
										}
									}
								}
							}
						}
						else if(filterJSON.has("genre"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesGenreNode = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
							for(String genre: filterJSON.getString("genre").split(","))
							{
								Node genreNode = genre_index.get(GENRE_NAME, genre.toLowerCase()).getSingle();
								if(genreNode != null && seriesGenreNode.equals(genreNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else if(filterJSON.has("language"))
						{
							Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
							Node seriesLangNode = series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
							for(String lang: filterJSON.getString("language").split(","))
							{
								Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();
								if(langNode != null && seriesLangNode.equals(langNode))
								{
									if(i < prev_cnt)
									{
										i++;
										continue;
									}
									
									i++;
									outputChaptersNode.addLast(chapter);
								}
							}
						}
						else // i.e., no need to apply filter
						{
							if(i < prev_cnt)
							{
								i++;
								continue;
							}
							
							i++;
							outputChaptersNode.addLast(chapter);
						}
					}
					else // i.e., no need to apply filter
					{
						if(i < prev_cnt)
						{
							i++;
							continue;
						}
						
						i++;
						outputChaptersNode.addLast(chapter);
					}
					
				}
				
				for(Node chapter : outputChaptersNode)
					jsonArray.put(getJSONForChapter(chapter, s_user_node));
				
				tx.success();
			}
			

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapters()");
			System.out.println("Something went wrong, while returning chapters from get_chapters  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();
	}
	
	
	private JSONObject getJSONForChapter(Node chapter, Node req_user)
	{
		JSONObject obj = new JSONObject();
		obj.put("P_Author_FullName",chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode().getProperty(FULL_NAME).toString());
		obj.put("P_Author",chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode().getProperty(USER_ID).toString());
		obj.put("P_Author_Status",chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode().getProperty(STATUS).toString());
		obj.put("P_Title_ID",chapter.getProperty(CHAPTER_TITLE_ID).toString());
		obj.put("P_Id",chapter.getProperty(CHAPTER_ID).toString());
		obj.put("P_Wc",chapter.getProperty(CHAPTER_WORDS_COUNT));
		
		Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
		
		if(!series.getProperty(SERIES_TYPE).toString().equals("2"))
		{
			obj.put("P_Feature_Image",series.getProperty(SERIES_FEAT_IMG));
			obj.put("P_Title",series.getProperty(SERIES_TITLE).toString());
		}
		else
		{
			obj.put("P_Feature_Image",chapter.getProperty(CHAPTER_FEAT_IMAGE));
			obj.put("P_Title",chapter.getProperty(CHAPTER_TITLE).toString());
		}
		obj.put("Free_Or_Paid", chapter.getProperty(CHAPTER_FREE_OR_PAID));
		
		JSONObject seriesJSON = new JSONObject();
		seriesJSON.put("Series_Id", series.getProperty(SERIES_ID).toString());
		seriesJSON.put("Series_Ttl", series.getProperty(SERIES_TITLE).toString());
		seriesJSON.put("Series_Tid", series.getProperty(SERIES_TITLE_ID).toString());
		seriesJSON.put("Series_Typ", series.getProperty(SERIES_TYPE));
		
		obj.put("Series_Info", seriesJSON);
		obj.put("P_Genre",series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode().getProperty(GENRE_NAME).toString());
		obj.put("P_Lang",series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode().getProperty(LANG_NAME).toString());
		obj.put("P_TimeCreated",chapter.getProperty(TIME_CREATED).toString());
		obj.put("P_Num_Views",getAccumulatedChapterViews(chapter));
			
//		obj.put("P_Num_Views",0);
		obj.put("P_Num_Reads",chapter.getProperty(TOTAL_CHAPTER_READS).toString());
		obj.put("P_Num_Fvrts",chapter.getDegree(USER_FAV_CHAPTER, Direction.INCOMING));
		obj.put("Is_Neo4j",true);
		
		int tot_rating = 0;
		Iterator<Relationship> ratingRelItr = chapter.getRelationships(Direction.INCOMING, USER_RATED_A_CHAPTER).iterator();
		while(ratingRelItr.hasNext())
			tot_rating = tot_rating + Integer.parseInt(ratingRelItr.next().getProperty(CHAPTER_RATING).toString());
		if(chapter.getDegree(USER_RATED_A_CHAPTER, Direction.INCOMING) == 0)
			obj.put("P_Rating",tot_rating);
		else
			obj.put("P_Rating",tot_rating/chapter.getDegree(USER_RATED_A_CHAPTER, Direction.INCOMING));

		if(req_user == null)
			obj.put("P_Is_Fvrt",0);
		else
		{
			obj.put("P_Is_Fvrt",0);			
			Iterator<Relationship> favRels = req_user.getRelationships(USER_FAV_CHAPTER, Direction.OUTGOING).iterator();
			while(favRels.hasNext())
			{
				if(favRels.next().getEndNode().equals(chapter))
				{
					obj.put("P_Is_Fvrt",1);
					break;
				}
			}
		}
		if(req_user == null)
			obj.put("P_Is_Bmrk",0);
		else
		{
			obj.put("P_Is_Bmrk",0);			
			Iterator<Relationship> bmRels = req_user.getRelationships(USER_BOOKMARK_CHAPTER, Direction.OUTGOING).iterator();
			while(bmRels.hasNext())
			{
				if(bmRels.next().getEndNode().equals(chapter))
				{
					obj.put("P_Is_Bmrk",1);
					break;
				}
			}
		}

		if(req_user == null)
			obj.put("P_Is_Red",0);
		else
		{
			obj.put("P_Is_Red",0);					
			Iterator<Relationship> isReadRels = req_user.getRelationships(USER_READ_A_CHAPTER, Direction.OUTGOING).iterator();
			while(isReadRels.hasNext())
			{
				Relationship rel = isReadRels.next();
				if(rel.getEndNode().equals(chapter))
				{
					if(rel.hasProperty(CHAPTER_READ_STATUS))
						obj.put("P_Is_Red",Integer.parseInt(rel.getProperty(CHAPTER_READ_STATUS).toString()));
					else obj.put("P_Is_Red", 1);
					break;
				}
			}
		}
		
		return obj;
	}
	
	
	private JSONObject getJSONForChapterClosed(Node chapter, Node req_user)
	{
		JSONObject obj = new JSONObject();
		obj.put("P_Author_FullName",chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER_TO_CLOSED_CONTEST, Direction.INCOMING).getStartNode().getProperty(FULL_NAME).toString());
		obj.put("P_Author",chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER_TO_CLOSED_CONTEST, Direction.INCOMING).getStartNode().getProperty(USER_ID).toString());
		obj.put("P_Author_Status",chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER_TO_CLOSED_CONTEST, Direction.INCOMING).getStartNode().getProperty(STATUS).toString());
		obj.put("P_Title_ID",chapter.getProperty(CHAPTER_TITLE_ID).toString());
		obj.put("P_Id",chapter.getProperty(CHAPTER_ID).toString());
		obj.put("P_Wc",chapter.getProperty(CHAPTER_WORDS_COUNT));
		
		Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
		
		if(!series.getProperty(SERIES_TYPE).toString().equals("2"))
		{
			obj.put("P_Feature_Image",series.getProperty(SERIES_FEAT_IMG));
			obj.put("P_Title",series.getProperty(SERIES_TITLE).toString());
		}
		else
		{
			obj.put("P_Feature_Image",chapter.getProperty(CHAPTER_FEAT_IMAGE));
			obj.put("P_Title",chapter.getProperty(CHAPTER_TITLE).toString());
		}
		obj.put("Free_Or_Paid", chapter.getProperty(CHAPTER_FREE_OR_PAID));
		
		JSONObject seriesJSON = new JSONObject();
		seriesJSON.put("Series_Id", series.getProperty(SERIES_ID).toString());
		seriesJSON.put("Series_Ttl", series.getProperty(SERIES_TITLE).toString());
		seriesJSON.put("Series_Tid", series.getProperty(SERIES_TITLE_ID).toString());
		seriesJSON.put("Series_Typ", series.getProperty(SERIES_TYPE));
		
		obj.put("Series_Info", seriesJSON);
		obj.put("P_Genre",series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode().getProperty(GENRE_NAME).toString());
		obj.put("P_Lang",series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode().getProperty(LANG_NAME).toString());
		obj.put("P_TimeCreated",chapter.getProperty(TIME_CREATED).toString());
		obj.put("P_Num_Views",getAccumulatedChapterViews(chapter));
			
//		obj.put("P_Num_Views",0);
		obj.put("P_Num_Reads",chapter.getProperty(TOTAL_CHAPTER_READS).toString());
		obj.put("P_Num_Fvrts",chapter.getDegree(USER_FAV_CHAPTER, Direction.INCOMING));
		obj.put("Is_Neo4j",true);
		
		int tot_rating = 0;
		Iterator<Relationship> ratingRelItr = chapter.getRelationships(Direction.INCOMING, USER_RATED_A_CHAPTER).iterator();
		while(ratingRelItr.hasNext())
			tot_rating = tot_rating + Integer.parseInt(ratingRelItr.next().getProperty(CHAPTER_RATING).toString());
		if(chapter.getDegree(USER_RATED_A_CHAPTER, Direction.INCOMING) == 0)
			obj.put("P_Rating",tot_rating);
		else
			obj.put("P_Rating",tot_rating/chapter.getDegree(USER_RATED_A_CHAPTER, Direction.INCOMING));

		if(req_user == null)
			obj.put("P_Is_Fvrt",0);
		else
		{
			obj.put("P_Is_Fvrt",0);			
			Iterator<Relationship> favRels = req_user.getRelationships(USER_FAV_CHAPTER, Direction.OUTGOING).iterator();
			while(favRels.hasNext())
			{
				if(favRels.next().getEndNode().equals(chapter))
				{
					obj.put("P_Is_Fvrt",1);
					break;
				}
			}
		}
		if(req_user == null)
			obj.put("P_Is_Bmrk",0);
		else
		{
			obj.put("P_Is_Bmrk",0);			
			Iterator<Relationship> bmRels = req_user.getRelationships(USER_BOOKMARK_CHAPTER, Direction.OUTGOING).iterator();
			while(bmRels.hasNext())
			{
				if(bmRels.next().getEndNode().equals(chapter))
				{
					obj.put("P_Is_Bmrk",1);
					break;
				}
			}
		}

		if(req_user == null)
			obj.put("P_Is_Red",0);
		else
		{
			obj.put("P_Is_Red",0);					
			Iterator<Relationship> isReadRels = req_user.getRelationships(USER_READ_A_CHAPTER, Direction.OUTGOING).iterator();
			while(isReadRels.hasNext())
			{
				Relationship rel = isReadRels.next();
				if(rel.getEndNode().equals(chapter))
				{
					if(rel.hasProperty(CHAPTER_READ_STATUS))
						obj.put("P_Is_Red",Integer.parseInt(rel.getProperty(CHAPTER_READ_STATUS).toString()));
					else obj.put("P_Is_Red", 1);
					break;
				}
			}
		}
		
		return obj;
	}

	
	private JSONObject getJSONForNanostory(Node nanostory, Node req_user)
	{
		JSONObject obj = new JSONObject();
		obj.put("P_Author_FullName",nanostory.getSingleRelationship(USER_WRITTEN_A_NANOSTORY, Direction.INCOMING).getStartNode().getProperty(FULL_NAME).toString());
		obj.put("P_Author",nanostory.getSingleRelationship(USER_WRITTEN_A_NANOSTORY, Direction.INCOMING).getStartNode().getProperty(USER_ID).toString());
		obj.put("P_Author_Status",nanostory.getSingleRelationship(USER_WRITTEN_A_NANOSTORY, Direction.INCOMING).getStartNode().getProperty(STATUS).toString());
		obj.put("P_Id",nanostory.getProperty(NANOSTORY_ID).toString());
		obj.put("P_Feature_Image",nanostory.getProperty(NANOSTORY_FEAT_IMG));
		obj.put("P_Content",nanostory.getProperty(NANOSTORY_CONTENT));
		obj.put("P_Genre",nanostory.getSingleRelationship(NANOSTORY_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode().getProperty(GENRE_NAME).toString());
		obj.put("P_Lang",nanostory.getSingleRelationship(NANOSTORY_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode().getProperty(LANG_NAME).toString());
		obj.put("P_TimeCreated",nanostory.getProperty(TIME_CREATED).toString());
		obj.put("P_Num_Views",nanostory.getProperty(NANOSTORY_VIEWS));
//		obj.put("P_Views",0);
		obj.put("P_Num_Likes", nanostory.getDegree(USER_LIKED_NANOSTORY));
		obj.put("P_Num_Dislikes", nanostory.getDegree(USER_DISLIKED_NANOSTORY));
		//User status on nanostory NOTHING [0] / LIKED [1] / DISLIKED [2]  
		if(req_user == null)
			obj.put("P_Like_Sts",0);
		else if (isRelationExistsBetween(USER_LIKED_NANOSTORY, req_user, nanostory))
			obj.put("P_Like_Sts",1);
		else if (isRelationExistsBetween(USER_DISLIKED_NANOSTORY, req_user, nanostory))
			obj.put("P_Like_Sts",2);
		else 
			obj.put("P_Like_Sts",0);
		
		obj.put("Is_Neo4j",true);
					
		return obj;
	}


	private JSONObject getJSONForUser(Node user, Node req_user)
	{
		JSONObject obj = new JSONObject();
		obj.put("U_FullName",user.getProperty(FULL_NAME).toString());
		obj.put("U_Id",user.getProperty(USER_ID).toString());
		
		if(user.hasProperty(BIO))
			obj.put("U_Bio",user.getProperty(BIO).toString());
		else
			obj.put("U_Bio","");
		
		obj.put("User_Status",user.getProperty(STATUS).toString());
		obj.put("Mobile_Dial_Code",user.getProperty(MOBILE_DIAL_CODE).toString());
		obj.put("U_Num_Writings",user.getDegree(USER_WRITTEN_A_CHAPTER , Direction.OUTGOING));
//		obj.put("U_Num_Readings",user.getDegree(USER_VIEWED_A_CHAPTER , Direction.OUTGOING));
//		obj.put("U_Num_Following",user.getDegree(USER_FOLLOW_USER, Direction.OUTGOING));
		obj.put("U_Num_Subscribers",user.getDegree(USER_FOLLOW_USER, Direction.INCOMING));
		if(req_user == null)
			obj.put("U_Is_Following",0);
		else
		{

			obj.put("U_Is_Following",0);
			
			Iterator<Relationship> followingRels = req_user.getRelationships(USER_FOLLOW_USER, Direction.OUTGOING).iterator();
			while(followingRels.hasNext())
			{
				if(followingRels.next().getEndNode().equals(user))
				{
					obj.put("U_Is_Following",1);
					break;
				}
			}
		}
		obj.put("Is_Neo4j",true);
		return obj;
	}
	
	private JSONObject getShortJSONForUser(Node user, Node req_user)
	{
		JSONObject obj = new JSONObject();
		obj.put("FullName",user.getProperty(FULL_NAME).toString());
		obj.put("UserId",user.getProperty(USER_ID).toString());
		obj.put("User_Status",user.getProperty(STATUS).toString());
		obj.put("Is_Neo4j",true);
		return obj;
	}
	
	private JSONObject getJSONForComment(Node comment)
	{
		JSONObject obj = new JSONObject();
		obj.put("Cm_By_Fullname",comment.getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING).getStartNode().getProperty(FULL_NAME).toString());
		obj.put("Cm_By_UserId",comment.getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING).getStartNode().getProperty(USER_ID).toString());
		obj.put("Cm_By_UserStatus",comment.getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING).getStartNode().getProperty(STATUS).toString());
		obj.put("Cm_ID",comment.getProperty(COMMENT_ID).toString());
		if(comment.hasRelationship(COMMENT_WRITTEN_ON_CHAPTER))
			obj.put("Cm_CID",comment.getSingleRelationship(COMMENT_WRITTEN_ON_CHAPTER, Direction.OUTGOING).getEndNode().getProperty(CHAPTER_ID).toString());
		else if(comment.hasRelationship(COMMENT_WRITTEN_ON_CONTEST))
			obj.put("Cm_CID",comment.getSingleRelationship(COMMENT_WRITTEN_ON_CONTEST, Direction.OUTGOING).getEndNode().getProperty(CONTEST_ID).toString());
		else
			obj.put("Cm_CID","");
		obj.put("Cm_Cntnt", comment.getProperty(COMMENT_CONTENT).toString());
		obj.put("Cm_TimeCreated",comment.getProperty(TIME_CREATED).toString());
		obj.put("Cm_Rply_Cnt",comment.getDegree(REPLY_COMMENT_WRITTEN_ON_COMMENT, Direction.INCOMING));
		obj.put("Is_Neo4j",true);
		return obj;
	}
	
	private JSONObject getJSONForSeries(Node series, Node user)
	{
		JSONObject obj = new JSONObject();		
		obj.put("P_Author_FullName",series.getSingleRelationship(USER_STARTED_SERIES, Direction.INCOMING).getStartNode().getProperty(FULL_NAME).toString());
		obj.put("P_Author",series.getSingleRelationship(USER_STARTED_SERIES, Direction.INCOMING).getStartNode().getProperty(USER_ID).toString());
		obj.put("P_Author_Status",series.getSingleRelationship(USER_STARTED_SERIES, Direction.INCOMING).getStartNode().getProperty(STATUS).toString());
		obj.put("P_Title_ID",series.getProperty(SERIES_TITLE_ID).toString());
		obj.put("P_Title",series.getProperty(SERIES_TITLE).toString());
		obj.put("P_Feature_Image",series.getProperty(SERIES_FEAT_IMG).toString());
		obj.put("P_Id",series.getProperty(SERIES_ID).toString());
		obj.put("P_Genre",series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode().getProperty(GENRE_NAME).toString());
		obj.put("P_Lang",series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode().getProperty(LANG_NAME).toString());
		obj.put("P_TimeCreated",series.getProperty(TIME_CREATED).toString());
		obj.put("P_Num_Views",getAccumulatedSeriesViews(series));
//		obj.put("P_Num_Views",0);
		obj.put("P_Num_Fvrts",0);
		obj.put("P_Rating",0);
		obj.put("P_Type", series.getProperty(SERIES_TYPE).toString());
		obj.put("P_Num_Chapters", series.getDegree(CHAPTER_BELONGS_TO_SERIES));
		obj.put("P_Num_Subscribers", series.getDegree(USER_SUBSCRIBED_TO_SERIES));
		
		if(series.hasRelationship(SERIES_STARTED_REL_TO_BASE_NODE))
			obj.put("P_Og_Sts", 0);
		else if(series.hasRelationship(SERIES_ONGOING_REL_TO_BASE_NODE))
			obj.put("P_Og_Sts", 1);
		else if(series.hasRelationship(SERIES_COMPLETED_REL_TO_BASE_NODE))
			obj.put("P_Og_Sts", 2);
		
		if(user == null)
			obj.put("Is_Subscribe",0);
		else
		{
			obj.put("Is_Subscribe",0);			
			Iterator<Relationship> followingRels = user.getRelationships(USER_SUBSCRIBED_TO_SERIES, Direction.OUTGOING).iterator();
			while(followingRels.hasNext())
			{
				if(followingRels.next().getEndNode().equals(series))
				{
					obj.put("Is_Subscribe",1);
					break;
				}
			}
		}

		obj.put("Is_Neo4j",true);
		return obj;
	}
	
	private JSONObject getJSONForAnthologies(Node anthology, Node user)
	{
		JSONObject obj = new JSONObject();		
		obj.put("P_Author_FullName",anthology.getSingleRelationship(USER_STARTED_ANTHOLOGY, Direction.INCOMING).getStartNode().getProperty(FULL_NAME).toString());
		obj.put("P_Author",anthology.getSingleRelationship(USER_STARTED_ANTHOLOGY, Direction.INCOMING).getStartNode().getProperty(USER_ID).toString());
		obj.put("P_Author_Status",anthology.getSingleRelationship(USER_STARTED_ANTHOLOGY, Direction.INCOMING).getStartNode().getProperty(STATUS).toString());
		obj.put("P_Title_ID",anthology.getProperty(ANTHOLOGY_TITLE_ID).toString());
		obj.put("P_Title",anthology.getProperty(ANTHOLOGY_TITLE).toString());
		obj.put("P_Feature_Image",anthology.getProperty(ANTHOLOGY_FEAT_IMG).toString());
		obj.put("P_Id",anthology.getProperty(ANTHOLOGY_ID).toString());
		
		
		JSONArray list = new JSONArray();
		Iterator<Relationship> belongstoRels = anthology.getRelationships(ANTHOLOGY_BELONGS_TO_GENRE, Direction.OUTGOING).iterator();
		while(belongstoRels.hasNext())
		{
			list.put(belongstoRels.next().getEndNode().getProperty(GENRE_NAME.toString()));	
		}
		obj.put("P_Genre",list);
		obj.put("P_Lang",anthology.getSingleRelationship(ANTHOLOGY_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode().getProperty(LANG_NAME).toString());
		obj.put("P_TimeCreated",anthology.getProperty(TIME_CREATED).toString());
		obj.put("P_Num_Chapters", anthology.getDegree(ANTHOLOGY_TAGGED_A_POST));
//		obj.put("P_Num_Subscribers", anthology.getDegree(USER_SUBSCRIBED_TO_ANTHOLOGY));
		
//		if(user == null)
//			obj.put("Is_Subscribe",0);
//		else
//		{
//			obj.put("Is_Subscribe",0);			
//			Iterator<Relationship> followingRels = user.getRelationships(USER_SUBSCRIBED_TO_ANTHOLOGY, Direction.OUTGOING).iterator();
//			while(followingRels.hasNext())
//			{
//				if(followingRels.next().getEndNode().equals(anthology))
//				{
//					obj.put("Is_Subscribe",1);
//					break;
//				}
//			}
//		}

		obj.put("Is_Neo4j",true);
		return obj;
	}

	
	private JSONObject getJSONForContest(Node contest, Node user)
	{
		JSONObject obj = new JSONObject();		
		obj.put("P_Author_FullName",contest.getSingleRelationship(USER_STARTED_CONTEST, Direction.INCOMING).getStartNode().getProperty(FULL_NAME).toString());
		obj.put("P_Author",contest.getSingleRelationship(USER_STARTED_CONTEST, Direction.INCOMING).getStartNode().getProperty(USER_ID).toString());
		obj.put("P_Author_Status",contest.getSingleRelationship(USER_STARTED_CONTEST, Direction.INCOMING).getStartNode().getProperty(STATUS).toString());
		obj.put("P_Smry",contest.getProperty(CONTEST_SUMMARY).toString());
		obj.put("P_Title_ID",contest.getProperty(CONTEST_TITLE_ID).toString());
		obj.put("P_Title",contest.getProperty(CONTEST_TITLE).toString());
		obj.put("P_Feature_Image",contest.getProperty(CONTEST_FEAT_IMAGE).toString());
		obj.put("P_Id",contest.getProperty(CONTEST_ID).toString());
		obj.put("P_Lang",contest.getProperty(CONTEST_LANGUAGE).toString());
		obj.put("P_TimeCreated",contest.getProperty(TIME_CREATED).toString());
		obj.put("P_StartDt",contest.getProperty(CONTEST_START_DATE).toString());
		obj.put("P_EndDt",contest.getProperty(CONTEST_END_DATE).toString());
		obj.put("P_Num_Chapters", contest.getDegree(CHAPTER_BELONGS_TO_CONTEST));
		obj.put("P_Num_Series", contest.getDegree(SERIES_BELONGS_TO_CONTEST));
		obj.put("P_Sub_Count", contest.getDegree(USER_SUBSCRIBED_TO_CONTEST));
		obj.put("Is_Neo4j",true);
		return obj;
	}
	
	private int getAccumulatedSeriesViews(Node series)
	{
		Iterator<Relationship> chaptersRelItr = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
		int views = 0;
		while(chaptersRelItr.hasNext())
			views = views + getAccumulatedChapterViews(chaptersRelItr.next().getStartNode());
			
		return views;
	}
	
	private int getAccumulatedChapterViews(Node chapter)
	{
		int views = 0;
		views = Integer.parseInt(chapter.getProperty(TOTAL_CHAPTER_VIEWS).toString());
			if(views >= 1000)
				views = views + 1000;
			else if (views >= 500 && views < 1000)
				views = views + 500;
			else if (views >= 100 && views < 500)
				views = views + 400;
			else if (views >=50 && views < 100)
				views = views + 50;
			else if (views >=10 && views < 50)
				views = views + 40;
		return views;
	}
	
	private JSONObject getShortJSONForSeries(Node series, Node user)
	{
		JSONObject obj = new JSONObject();		
		obj.put("P_Id",series.getProperty(SERIES_ID).toString());
		obj.put("P_Title_ID",series.getProperty(SERIES_TITLE_ID).toString());
		obj.put("P_Title",series.getProperty(SERIES_TITLE).toString());
		obj.put("P_Feature_Image",series.getProperty(SERIES_FEAT_IMG).toString());
		obj.put("P_Num_Subscribers", series.getDegree(USER_SUBSCRIBED_TO_SERIES));
		if(user == null)
			obj.put("Is_Subscribe",0);
		else
		{
			obj.put("Is_Subscribe",0);			
			Iterator<Relationship> followingRels = user.getRelationships(USER_SUBSCRIBED_TO_SERIES, Direction.OUTGOING).iterator();
			while(followingRels.hasNext())
			{
				if(followingRels.next().getEndNode().equals(series))
				{
					obj.put("Is_Subscribe",1);
					break;
				}
			}
		}
		
		obj.put("P_Lang", series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode().getProperty(LANG_NAME));

		obj.put("Is_Neo4j",true);
		return obj;
	}

	@Override
	public String create_or_edit_comment(String chapter_id, String comment_id,
			String content, String parent_cmnt_id, String user_id, int time, int is_edit, String cmntType)
			throws TException {
		if(is_edit == 0)
			return create_comment(chapter_id, comment_id, content, parent_cmnt_id, user_id, time, cmntType);
		else if(is_edit == 1)
			return edit_comment(chapter_id, comment_id, content, parent_cmnt_id, user_id, time, cmntType);
		else return "false";		
	}
		
	public String create_comment(String chapter_id, String comment_id,
			String content, String parent_cmnt_id, String user_id, int time, String cmntType)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{

			if(comment_id == null || comment_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty comment id");
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty chapter id");
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty user id");
			if(parent_cmnt_id == null)
				parent_cmnt_id = "";
			if(content == null)
				content = "";
			
			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			Index<Node> commentId_index = graphDb.index().forNodes(COMMENT_ID_INDEX);
					
			
			Node userNode = userId_index.get(USER_ID,user_id).getSingle();
			if(userNode == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);

			Node chapter_node = chapterId_index.get(CHAPTER_ID,chapter_id).getSingle();
			
			Node contest_node = contestId_index.get(CONTEST_ID,chapter_id).getSingle();
			if(contest_node == null && cmntType.equalsIgnoreCase("CN"))
				throw new KahaniyaCustomException("Contest does not exists with given id : "+chapter_id);
			else if(chapter_node == null && cmntType.equalsIgnoreCase("C"))
				throw new KahaniyaCustomException("Chapter does not exists with given id : "+chapter_id);
			else if(!cmntType.equalsIgnoreCase("C") && !cmntType.equalsIgnoreCase("CN"))
				throw new KahaniyaCustomException("Invalid cmntType "+cmntType);
			
			Node parentCommentNode = null;
			if(parent_cmnt_id != null && !parent_cmnt_id.equals(""))
			parentCommentNode = commentId_index.get(COMMENT_ID,parent_cmnt_id).getSingle();
			
			
			if(commentId_index.get(COMMENT_ID,comment_id).getSingle() != null)
				throw new KahaniyaCustomException("Comment already exists with given id : "+comment_id);
			
			Node comment_node = Comment(comment_id, content, time);  // Creating a new comment node
			if(comment_node == null)
				throw new KahaniyaCustomException("Something went wrong, while creating comment ");

			//create relationship with user
			createRelation(USER_WRITTEN_A_COMMENT, userNode, comment_node, time);
			
			//create relationships with Chapter / Contest
			if(cmntType.equalsIgnoreCase("CN") && contest_node != null)createRelation(COMMENT_WRITTEN_ON_CONTEST, comment_node, contest_node, time);
			else if(cmntType.equalsIgnoreCase("C") && chapter_node != null)createRelation(COMMENT_WRITTEN_ON_CHAPTER, comment_node, chapter_node, time);

			//create relationships with parent commment
			if(parentCommentNode != null)
				createRelation(REPLY_COMMENT_WRITTEN_ON_COMMENT, comment_node, parentCommentNode, time);
			
			//Indexing newly created comment node
			commentId_index.add(comment_node, COMMENT_ID, comment_id);
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_comment()");
			System.out.println("Something went wrong, while creating comment from create_comment  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_comment()");
			System.out.println("Something went wrong, while creating comment from create_comment  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;

	}
	
	public String edit_comment(String chapter_id, String comment_id,
			String content, String parent_cmnt_id, String user_id, int time, String cmntType)
			throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(comment_id == null || comment_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty comment id");
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty chapter id");
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty user id");
			if(parent_cmnt_id == null)
				parent_cmnt_id = "";
			if(content == null)
				content = "";
			aquireWriteLock(tx);
			Index<Node> commentId_index = graphDb.index().forNodes(COMMENT_ID_INDEX);
			
			Node commentNode = commentId_index.get(COMMENT_ID,comment_id).getSingle();
			if(commentNode == null)
				throw new KahaniyaCustomException("Comment does not exists with given id : "+comment_id);
			
			commentNode.setProperty(COMMENT_CONTENT, content);
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_comment()");
			System.out.println("Something went wrong, while editing comment from edit_comment  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_comment()");
			System.out.println("Something went wrong, while editing comment from edit_comment  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}

	@Override
	public String delete_comment(String comment_id) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{

			if(comment_id == null || comment_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty comment id");
			aquireWriteLock(tx);
			Index<Node> commentId_index = graphDb.index().forNodes(COMMENT_ID_INDEX);
			
			Node commentNode = commentId_index.get(COMMENT_ID,comment_id).getSingle();
			if(commentNode == null)
				throw new KahaniyaCustomException("Comment does not exists with given id : "+comment_id);
			
			commentId_index.remove(commentNode);
			Iterator<Relationship> relItr = commentNode.getRelationships().iterator();
			while(relItr.hasNext())
				relItr.next().delete();
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_comment()");
			System.out.println("Something went wrong, while deleting comment from delete_comment  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_comment()");
			System.out.println("Something went wrong, while deleting comment from delete_comment  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public String get_comments(String chapter_id, int prev_cnt, int count, String cmntType) throws TException {

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty chapter id");
			aquireWriteLock(tx);
			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			
			Node chapter_node = chapterId_index.get(CHAPTER_ID,chapter_id).getSingle();
			Node contest_node = contestId_index.get(CONTEST_ID,chapter_id).getSingle();
			if(chapter_node == null && cmntType.equalsIgnoreCase("C"))
				throw new KahaniyaCustomException("Chapter does not exists with given id : "+chapter_id);
			else if(contest_node == null && cmntType.equalsIgnoreCase("CN"))
				throw new KahaniyaCustomException("Contest does not exists with given id : "+chapter_id);
			else if(!cmntType.equalsIgnoreCase("C") && !cmntType.equalsIgnoreCase("CN")) throw new KahaniyaCustomException("Invalid cmntType : "+cmntType);
			
			Iterator<Relationship> cmntsRelItr = chapter_node.getRelationships(COMMENT_WRITTEN_ON_CHAPTER,COMMENT_WRITTEN_ON_CONTEST).iterator();
			LinkedList<Relationship> cmntsRelList = new LinkedList<Relationship>();
			while(cmntsRelItr.hasNext())
				cmntsRelList.addLast(cmntsRelItr.next());
			Collections.sort(cmntsRelList, TimeCreatedComparatorForRelationships);
			
			int c = 0;
			for(Relationship rel : cmntsRelList)
			{
				if(c < prev_cnt)
					continue;
				else if ( c > prev_cnt+count)
					break;
				else
					jsonArray.put(getJSONForComment(rel.getStartNode()));
			}
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_comments()");
			System.out.println("Something went wrong, while returning comments from get_comments  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_comments()");
			System.out.println("Something went wrong, while returning comments from get_comments  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();
	}

	@Override
	public String get_all_items(String item_type, int prev_cnt, int count)
			throws TException {

		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			
			if(item_type == null || item_type.equals(""))
				throw new KahaniyaCustomException("Invalid item type");
			if(item_type.equalsIgnoreCase("C"))
			{
				int c = 0;

				Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
				ResourceIterator<Node> chaptersItr = chapterId_index.query(CHAPTER_ID, "*").iterator();
				LinkedList<Node> chaptersList = new LinkedList<Node>();
				while(chaptersItr.hasNext())
				{
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					else if ( c > prev_cnt+count)
						break;
					else
						chaptersList.addLast(chaptersItr.next());
					c++;
				}
				
				Collections.sort(chaptersList, TimeCreatedComparatorForNodes);
			
				for(Node chapter : chaptersList)
					jsonArray.put(getJSONForChapter(chapter, null));
			}
			else if(item_type.equalsIgnoreCase("CN"))
			{
				int c = 0;

				Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
				ResourceIterator<Node> contestsItr = contestId_index.query(CONTEST_ID, "*").iterator();
				LinkedList<Node> contestsList = new LinkedList<Node>();
				while(contestsItr.hasNext())
				{
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					else if ( c > prev_cnt+count)
						break;
					else
						contestsList.addLast(contestsItr.next());
					c++;
				}
				
				Collections.sort(contestsList, TimeCreatedComparatorForNodes);
			
				for(Node contest : contestsList)
					jsonArray.put(getJSONForContest(contest, null));
			}
			else if(item_type.equalsIgnoreCase("S"))
			{
				int c = 0;

				Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
				ResourceIterator<Node> seriesItr = seriesId_index.query(SERIES_ID, "*").iterator();
				LinkedList<Node> seriesList = new LinkedList<Node>();
				while(seriesItr.hasNext())
				{
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					else if ( c > prev_cnt+count)
						break;
					else
						seriesList.addLast(seriesItr.next());
					c++;
				}
				
				Collections.sort(seriesList, TimeCreatedComparatorForNodes);
			
				for(Node series : seriesList)
					jsonArray.put(getJSONForSeries(series, null));
			}
			else if(item_type.equalsIgnoreCase("A") || item_type.equalsIgnoreCase("U"))
			{
				int c = 0;

				Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
				ResourceIterator<Node> userItr = userId_index.query(USER_ID, "*").iterator();
				LinkedList<Node> userList = new LinkedList<Node>();
				while(userItr.hasNext())
				{
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					else if ( c > prev_cnt+count)
						break;
					else
						userList.addLast(userItr.next());
					c++;
				}
				
				Collections.sort(userList, TimeCreatedComparatorForNodes);
			
				for(Node user : userList)
					jsonArray.put(getJSONForUser(user, null));
			}
			else if(item_type.equalsIgnoreCase("R"))
			{
				int c = 0;

				Index<Node> reviewId_index = graphDb.index().forNodes(REVIEW_ID_INDEX);
				ResourceIterator<Node> reviewItr = reviewId_index.query(REVIEW_ID, "*").iterator();
				LinkedList<Node> reviewList = new LinkedList<Node>();
				while(reviewItr.hasNext())
				{
					if(c < prev_cnt)
					{
						c++;
						continue;
					}
					else if ( c > prev_cnt+count)
						break;
					else
						reviewList.addLast(reviewItr.next());
					c++;
				}
				
				Collections.sort(reviewList, TimeCreatedComparatorForNodes);
			
				for(Node review : reviewList)
					jsonArray.put(getJSONForReview(review));
			}
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_all_items()");
			System.out.println("Something went wrong, while returning items from get_all_items  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_all_items()");
			System.out.println("Something went wrong, while returning items from get_all_items  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();		
	}

	@Override
	public String get_item_details(String item_type, String item_id)
			throws TException {

		JSONObject jsonObject = new JSONObject();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);

			if(item_type == null || item_type.equals(""))
				throw new KahaniyaCustomException("Invalid item type");
			if(item_id == null || item_id.equals(""))
				throw new KahaniyaCustomException("Invalid item id");
			
			if(item_type.equalsIgnoreCase("C"))
			{
				Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
				Node chapter = chapterId_index.get(CHAPTER_ID, item_id).getSingle();
				if(chapter == null)
					throw new KahaniyaCustomException("Invalid chapter id");
				jsonObject = getJSONForChapter(chapter, null);
			}
			else if(item_type.equalsIgnoreCase("S"))
			{
				Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
				Node series = seriesId_index.get(SERIES_ID, item_id).getSingle();
				if(series == null)
					throw new KahaniyaCustomException("Invalid series id");
				jsonObject = getJSONForSeries(series, null);
			}
			else if(item_type.equalsIgnoreCase("A") || item_type.equalsIgnoreCase("U"))
			{
				Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
				Node user = userId_index.get(USER_ID, item_id).getSingle();
				if(user == null)
					throw new KahaniyaCustomException("Invalid user id");
				jsonObject = getJSONForUser(user, null);
			}
			else if(item_type.equalsIgnoreCase("R"))
			{
				Index<Node> reviewId_index = graphDb.index().forNodes(REVIEW_ID_INDEX);
				Node review = reviewId_index.get(REVIEW_ID, item_id).getSingle();
				if(review == null)
					throw new KahaniyaCustomException("Invalid review id");
				jsonObject = getJSONForReview(review);
			}
			
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_item_details()");
			System.out.println("Something went wrong, while returning item_details  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonObject = new JSONObject();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_item_details()");
			System.out.println("Something went wrong, while returning item_details  :"+ex.getMessage());
			ex.printStackTrace();
			jsonObject = new JSONObject();
		}
		return jsonObject.toString();
	}

	@Override
	public String get_subscriptions_for_user(String user_id, int prev_cnt,
			int count, String lang) throws TException {
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);

			if(user_id == null)
				user_id = "";
			if(lang == null)
				lang = "";
			
			int c = 0;

			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
			Node user = userId_index.get(USER_ID, user_id).getSingle();
			Node langNode = lang_index.get(LANG_NAME, lang.toLowerCase()).getSingle();			
			
			if(langNode == null)
				throw new KahaniyaCustomException("No language node found with the given lang name:"+lang);
			
			LinkedList<Node> seriesList = new LinkedList<Node>();
			
			Iterator<Relationship> seriesItr = langNode.getRelationships(SERIES_BELONGS_TO_LANGUAGE).iterator();
			while(seriesItr.hasNext())
				seriesList.addLast(seriesItr.next().getStartNode());
			
			Collections.sort(seriesList, TrendingComparatorForSeriesNodes);
		
			LinkedList<Node> outputNodes = new LinkedList<Node>();
			LinkedList<Node> followingSeriesList = new LinkedList<Node>();
			if(user != null)
			{
				Iterator<Relationship> followingSeriesItr = user.getRelationships(USER_SUBSCRIBED_TO_SERIES, USER_STARTED_SERIES, USER_SKIP_SUBSCRIBED_TO_SERIES).iterator();
				while(followingSeriesItr.hasNext())
					followingSeriesList.addLast(followingSeriesItr.next().getEndNode());
				
			}
			
			for(Node series : seriesList)
			{

				if(followingSeriesList.contains(series) || series.getProperty(SERIES_TYPE).toString().equals("1"))
					continue;
				if(c < prev_cnt)
				{
					c ++;
					continue;
				}
				if(c >= count + prev_cnt)
					break;
				
				outputNodes.addLast(series);
				c++;
			}
			
			for(Node series: outputNodes)
				jsonArray.put(getShortJSONForSeries(series, user));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_subscriptions_for_user()");
			System.out.println("Something went wrong, while returning subscription suggestions from get_subscriptions_for_user  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_subscriptions_for_user()");
			System.out.println("Something went wrong, while returning subscription suggestions get_subscriptions_for_user  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();		

	}
	
	@Override
	public String get_top_authors( int prev_cnt, int count,
			String user_id) throws TException {
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			
			if(user_id == null)
				user_id = "";
			
			int c = 0;

			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Node user = userId_index.get(USER_ID, user_id).getSingle();

			ResourceIterator<Node> allUsersItr = userId_index.query(USER_ID, "*").iterator();
				
			LinkedList<Node> authorsList = new LinkedList<Node>();
			while(allUsersItr.hasNext())
			{
				Node n = allUsersItr.next();
				if(n.getDegree(USER_WRITTEN_A_CHAPTER) > 0 && !n.equals(user) && !isRelationExistsBetween(USER_FOLLOW_USER, user, n) && !isRelationExistsBetween(USER_SKIP_FOLLOW_USER, user, n))
					authorsList.addLast(n);
			}
			
			Collections.sort(authorsList, TrendingComparatorForAuthorNodes);
		
			LinkedList<Node> outputNodes = new LinkedList<Node>();
			for(Node author : authorsList)
			{
				if(c < prev_cnt)
				{
					c ++;
					continue;
				}
				if(c >= count + prev_cnt)
					break;
				outputNodes.addLast(author);
				c++;
			}
			
			for(Node author: outputNodes)
				jsonArray.put(getShortJSONForUser(author, user));
			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_top_authors()");
			System.out.println("Something went wrong, while returning top authors from get_top_authors  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_top_authors()");
			System.out.println("Something went wrong, while returning top authors from get_top_authors  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();		

	}

	@Override
	public String get_stats( String stats_type) throws TException {
		JSONObject jsonObj = new JSONObject();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);
			
			int tot_users = 0;
			int tot_authors = 0;
			
			int tot_series = 0;
			int tot_short_series = 0;
			int tot_series_without_any_chapters = 0;
			int tot_chapters = 0;
			int tot_reviews = 0;
			
			int tot_comments = 0;
			
			JSONObject langInfo = new JSONObject();
			JSONObject genreInfo = new JSONObject();
			
			if("U".equalsIgnoreCase(stats_type))
			{

				Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
				ResourceIterator<Node> allUsersItr = userId_index.query(USER_ID, "*").iterator();
			
				while(allUsersItr.hasNext())
				{
					tot_users++;
					if(allUsersItr.next().hasRelationship(USER_WRITTEN_A_CHAPTER))
						tot_authors++;
				}
				jsonObj.put("Total_Num_Of_Users", tot_users);
				jsonObj.put("Total_Num_Of_Users_Started_Writing", tot_authors);

			}
			else if("S".equalsIgnoreCase(stats_type))
			{
				Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
				ResourceIterator<Node> allSeriesItr = seriesId_index.query(SERIES_ID, "*").iterator();
				while(allSeriesItr.hasNext())
				{
					tot_series++;
					Node series = allSeriesItr.next();
					if(series.getProperty(SERIES_TYPE).toString().equals("1"))
						tot_short_series++;
					if(series.getDegree(CHAPTER_BELONGS_TO_SERIES) == 0)
						tot_series_without_any_chapters++;
				}
				jsonObj.put("Total_Num_Of_Series", tot_series);
				jsonObj.put("Total_Num_Of_Short_Series", tot_short_series);
				jsonObj.put("Total_Num_Of_Series_Without_Stories", tot_series_without_any_chapters);

			}
			else if("CH".equalsIgnoreCase(stats_type))
			{
				Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
				ResourceIterator<Node> allChaptersItr = chapterId_index.query(CHAPTER_ID, "*").iterator();
				while(allChaptersItr.hasNext())
				{
					tot_chapters ++;
					allChaptersItr.next();
				}
				jsonObj.put("Total_Num_Of_Chapters", tot_chapters);
			}
			else if("R".equalsIgnoreCase(stats_type))
			{
				Index<Node> reviewId_index = graphDb.index().forNodes(REVIEW_ID_INDEX);
				ResourceIterator<Node> allReviewsItr = reviewId_index.query(REVIEW_ID, "*").iterator();
				while(allReviewsItr.hasNext())
				{
					tot_reviews ++;
					allReviewsItr.next();
				}
				jsonObj.put("Total_Num_Of_Reviews", tot_reviews);
			}
			else if("C".equalsIgnoreCase(stats_type))
			{
				Index<Node> commentId_index = graphDb.index().forNodes(COMMENT_ID_INDEX);
				ResourceIterator<Node> allCommentsItr = commentId_index.query(COMMENT_ID, "*").iterator();
				while(allCommentsItr.hasNext())
				{
					tot_comments++;
					allCommentsItr.next();
				}
				jsonObj.put("Total_Num_Of_Comments", tot_comments);

			}
			else if("L".equalsIgnoreCase(stats_type))
			{
			
				Index<Node> lang_index = graphDb.index().forNodes(LANG_NAME_INDEX);
				ResourceIterator<Node> allLangsItr = lang_index.query(LANG_NAME, "*").iterator();
				while(allLangsItr.hasNext())
				{
					Node l = allLangsItr.next();
					langInfo.put(l.getProperty(LANG_NAME).toString(), l.getDegree(USER_INTERESTED_LANGUAGE));
				}
				jsonObj.put("lang", langInfo);
			}
			else if("G".equalsIgnoreCase(stats_type))
			{
			
				Index<Node> genre_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
				ResourceIterator<Node> allGenresItr = genre_index.query(GENRE_NAME, "*").iterator();
				while(allGenresItr.hasNext())
				{
					Node g = allGenresItr.next();
					genreInfo.put(g.getProperty(GENRE_NAME).toString(), g.getDegree(USER_INTERESTED_GENRE));
				}
				jsonObj.put("genre", genreInfo);
			}

			
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_stats()");
			System.out.println("Something went wrong, while returning stats from get_stats  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonObj = new JSONObject();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_stats()");
			System.out.println("Something went wrong, while returning stats from get_stats  :"+ex.getMessage());
			ex.printStackTrace();
			jsonObj = new JSONObject();
		}
		return jsonObj.toString();		

	}

	@Override
	public String delete_chapter(String chapter_id) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			
			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the parameter chapter_id");

			Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
			Index<Node> chapterTitleId_index = graphDb.index().forNodes(CHAPTER_TITLE_ID_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Node chapter = chapterId_index.get(CHAPTER_ID,chapter_id).getSingle();
			
			if(chapter == null)
				throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+chapter_id);

			deleteChapterRelationships(chapter);
			
			chapterId_index.remove(chapter);
			chapterTitleId_index.remove(chapter);
			search_index.remove(chapter);
			chapter.delete();
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_chapter()");
			System.out.println("Something went wrong, while deleting chapter from delete_chapter  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_chapter()");
			System.out.println("Something went wrong, while deleting chapter from delete_chapter  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;	
	}
	
	private void deleteChapterRelationships(Node chapter)
	{

		Index<Relationship> chapterViewsIndex = graphDb.index().forRelationships(USER_VIEWED_CHAPTER_REL_INDEX);
		Index<Relationship> chapterReadsIndex = graphDb.index().forRelationships(USER_READ_CHAPTER_REL_INDEX);
		Index<Node> commentId_index = graphDb.index().forNodes(COMMENT_ID_INDEX);
		
		Iterator<Relationship> relsItr = chapter.getRelationships(USER_VIEWED_A_CHAPTER).iterator();
		while(relsItr.hasNext())
			chapterViewsIndex.remove(relsItr.next());

		relsItr = chapter.getRelationships(USER_READ_A_CHAPTER).iterator();
		while(relsItr.hasNext())
			chapterReadsIndex.remove(relsItr.next());

		relsItr = chapter.getRelationships(COMMENT_WRITTEN_ON_CHAPTER).iterator();
		while(relsItr.hasNext())
		{
			Relationship rel =  relsItr.next();
			Node cmntNode = rel.getStartNode();
			rel.delete();
			
			commentId_index.remove(cmntNode);
			deleteSubComments(cmntNode);
			Iterator<Relationship> cmntRels = cmntNode.getRelationships().iterator();
			while(cmntRels.hasNext())
				cmntRels.next().delete();
			cmntNode.delete();
		}

		
		relsItr = chapter.getRelationships().iterator();
		while(relsItr.hasNext())
			relsItr.next().delete();
	}
	
	private void deleteContestRelationships(Node contest)
	{

		Index<Node> commentId_index = graphDb.index().forNodes(COMMENT_ID_INDEX);
		
		Iterator<Relationship> relsItr = contest.getRelationships(COMMENT_WRITTEN_ON_CONTEST).iterator();
		while(relsItr.hasNext())
		{
			Relationship rel =  relsItr.next();
			Node cmntNode = rel.getStartNode();
			rel.delete();
			
			commentId_index.remove(cmntNode);
			deleteSubComments(cmntNode);
			Iterator<Relationship> cmntRels = cmntNode.getRelationships().iterator();
			while(cmntRels.hasNext())
				cmntRels.next().delete();
			cmntNode.delete();
		}

		
		relsItr = contest.getRelationships().iterator();
		while(relsItr.hasNext())
			relsItr.next().delete();
	}
	
	private void deleteSeriesRelationships(Node series)
	{

		Index<Node> chapterId_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);
		Index<Node> chapterTitleId_index = graphDb.index().forNodes(CHAPTER_TITLE_ID_INDEX);
		Index<Node> reviewId_index = graphDb.index().forNodes(REVIEW_ID_INDEX);

		Iterator<Relationship> relsItr = series.getRelationships(REVIEW_BELONGS_TO_SERIES).iterator();
		while(relsItr.hasNext())
		{
			Relationship rel =  relsItr.next();
			Node reviewNode = rel.getStartNode();
			rel.delete();
			
			reviewId_index.remove(reviewNode);
			Iterator<Relationship> reviewRels = reviewNode.getRelationships().iterator();
			while(reviewRels.hasNext())
				reviewRels.next().delete();
			reviewNode.delete();
		}
		
		relsItr = series.getRelationships(CHAPTER_BELONGS_TO_SERIES).iterator();
		while(relsItr.hasNext())
		{
			Relationship rel =  relsItr.next();
			Node chapterNode = rel.getStartNode();
			rel.delete();

			chapterId_index.remove(chapterNode);
			chapterTitleId_index.remove(chapterNode);
			deleteChapterRelationships(chapterNode);
			chapterNode.delete();
		}

		
		relsItr = series.getRelationships().iterator();
		while(relsItr.hasNext())
			relsItr.next().delete();
	}
	
	private void deleteSubComments(Node comment)
	{

		Index<Node> commentId_index = graphDb.index().forNodes(COMMENT_ID_INDEX);
		
		Iterator<Relationship> relItr = comment.getRelationships(REPLY_COMMENT_WRITTEN_ON_COMMENT).iterator();
		while(relItr.hasNext())
		{
			Relationship rel = relItr.next();
			Node cmntNode = rel.getStartNode();
			rel.delete();
			
			deleteSubComments(cmntNode);
			commentId_index.remove(cmntNode);

			Iterator<Relationship> cmntRels = cmntNode.getRelationships().iterator();
			while(cmntRels.hasNext())
				cmntRels.next().delete();
			
			cmntNode.delete();
		}
		
	}

	@Override
	public String delete_user(String user_id) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the parameter user_id");

			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> userName_index = graphDb.index().forNodes(USER_NAME_INDEX);
			Index<Node> userEmail_index = graphDb.index().forNodes(USER_EMAIL_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Node user = userId_index.get(USER_ID,user_id).getSingle();
			
			if(user == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);

			userId_index.remove(user);
			userName_index.remove(user);
			userEmail_index.remove(user);
			search_index.remove(user);
			user.setProperty(IS_DELETED,"1");
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_user()");
			System.out.println("Something went wrong, while deleting user from delete_user  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_user()");
			ex.printStackTrace();
			res = "false";
		}
		return res;	
	}

	@Override
	public String delete_series(String series_id) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the parameter series_id");

			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> seriesTitleId_index = graphDb.index().forNodes(SERIES_TITLE_ID_INDEX);
			Index<Node> seriesType_index = graphDb.index().forNodes(SERIES_TYPE_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Node series = seriesId_index.get(SERIES_ID,series_id).getSingle();
			
			if(series == null)
				throw new KahaniyaCustomException("Series doesnot exists with given id : "+series_id);
			
			deleteSeriesRelationships(series);

			seriesId_index.remove(series);
			seriesTitleId_index.remove(series);
			seriesType_index.remove(series);
			search_index.remove(series);
			series.delete();
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_series()");
			System.out.println("Something went wrong, while deleting series from delete_series  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_series()");
			System.out.println("Something went wrong, while deleting series from delete_series  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;	
	}
	
	@Override
	public String delete_anthology(String anthology_id) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			
			if(anthology_id == null || anthology_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the parameter anthology_id");

			Index<Node> anthologyId_index = graphDb.index().forNodes(ANTHOLOGY_ID_INDEX);
			Index<Node> anthologyTitleId_index = graphDb.index().forNodes(ANTHOLOGY_TITLE_ID_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Node anthology = anthologyId_index.get(ANTHOLOGY_ID,anthology_id).getSingle();
			
			if(anthology == null)
				throw new KahaniyaCustomException("Series doesnot exists with given id : "+anthology_id);
			
			// Delete Relationships of Anthology
			Iterator<Relationship> relsItr = anthology.getRelationships().iterator();
			while(relsItr.hasNext())
				relsItr.next().delete();
			
			anthologyId_index.remove(anthology);
			anthologyTitleId_index.remove(anthology);
			search_index.remove(anthology);
			anthology.delete();
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_anthology()");
			System.out.println("Something went wrong, while deleting anthology from delete_anthology  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_anthology()");
			System.out.println("Something went wrong, while deleting anthology from delete_anthology  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;	
	}
	
	@Override
	public String delete_nanostory(String nanostory_id) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			
			if(nanostory_id == null || nanostory_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the parameter nanostory_id");

			Index<Node> nanostoryId_index = graphDb.index().forNodes(NANOSTORY_ID_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Node nanostory = nanostoryId_index.get(NANOSTORY_ID,nanostory_id).getSingle();
			
			if(nanostory == null)
				throw new KahaniyaCustomException("Nanostory doesnot exists with given id : "+nanostory_id);
			
			// Delete Relationships of Anthology
			Iterator<Relationship> relsItr = nanostory.getRelationships().iterator();
			while(relsItr.hasNext())
				relsItr.next().delete();
			
			nanostoryId_index.remove(nanostory);
			search_index.remove(nanostory);
			nanostory.delete();
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_nanostory()");
			System.out.println("Something went wrong, while deleting anthology from delete_nanostory  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_nanostory()");
			System.out.println("Something went wrong, while deleting anthology from delete_nanostory  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;	
	}


	
	@Override
	public String search(String query, int tp, String user_id, int prev_cnt, int count)
			throws TException {
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);

			if(query == null || query.length() == 0)
				throw new KahaniyaCustomException("Empty value receieved for the parameter query");

			Index<Node> user_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			Node s_user = user_index.get(USER_ID, user_id).getSingle();
			ResourceIterator<Node> userItr = null;
			ResourceIterator<Node> seriesItr = null;
			ResourceIterator<Node> chapterItr = null;
			ResourceIterator<Node> anthologyItr = null;
			ResourceIterator<Node> nanostoryItr = null;
			
			if(tp == 0) //all
			{
				userItr = search_index.query(SEARCH_USER, "*"+query.toLowerCase()+"*").iterator();
				seriesItr = search_index.query(SEARCH_SERIES, "*"+query.toLowerCase()+"*").iterator();
				chapterItr = search_index.query(SEARCH_CHAPTER, "*"+query.toLowerCase()+"*").iterator();
				anthologyItr = search_index.query(SEARCH_ANTHOLOGY, "*"+query.toLowerCase()+"*").iterator();
				nanostoryItr = search_index.query(SEARCH_NANOSTORY, "*"+query.toLowerCase()+"*").iterator();
				int  i = 0;
				JSONArray usersArray = new JSONArray();
				while(userItr.hasNext() && i < 6)
				{
					usersArray.put(getJSONForUser(userItr.next(),s_user));
//					
//					JSONObject obj = new JSONObject();
//					Node user = userItr.next();
//					obj.put("U_FullName", user.getProperty(FULL_NAME).toString());
//					obj.put("U_Id", user.getProperty(USER_ID).toString());
//					usersArray.put(obj);
					i++;
				}
				JSONObject usersData = new JSONObject();
				usersData.put("tp", 1);
				usersData.put("data", usersArray);
				usersData.put("ftp", "SA");
				
				i = 0;
				JSONArray seriesArray = new JSONArray();
				while(seriesItr.hasNext() && i < 6)
				{
//					JSONObject obj1 = new JSONObject();
//					Node series = seriesItr.next();
//					
					// no short stories
					if(!seriesItr.next().getProperty(SERIES_TYPE).toString().equals("2"))
						continue;
					
					seriesArray.put(getJSONForSeries(seriesItr.next(),s_user));
					
//					obj1.put("P_Title", series.getProperty(SERIES_TITLE).toString());
//					obj1.put("P_Id", series.getProperty(SERIES_ID).toString());
//					obj1.put("P_Title_ID", series.getProperty(SERIES_TITLE_ID).toString()); //lang & author and no short series
//					obj1.put("P_Lang", series.getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode().getProperty(LANG_NAME).toString());
//					obj1.put("P_Author_FullName", series.getSingleRelationship(USER_STARTED_SERIES, Direction.INCOMING).getStartNode().getProperty(FULL_NAME).toString());
//					obj1.put("P_Author", series.getSingleRelationship(USER_STARTED_SERIES, Direction.INCOMING).getStartNode().getProperty(USER_ID).toString());
//					obj1.put("P_Feature_Image",series.getProperty(SERIES_FEAT_IMG));
//					seriesArray.put(obj1);
					i++;
				}
				JSONObject seriesData = new JSONObject();
				seriesData.put("tp", 2);
				seriesData.put("data", seriesArray);
				seriesData.put("ftp", "SS");

				i = 0;
				JSONArray chapterArray = new JSONArray();
				while(chapterItr.hasNext() && i < 6)
				{
					chapterArray.put(getJSONForChapter(chapterItr.next(),s_user));
					
//					JSONObject obj2 = new JSONObject();
//					Node chapter = chapterItr.next();
//					obj2.put("P_Title", chapter.getProperty(CHAPTER_TITLE).toString()); // lang & autho
//					obj2.put("P_Id", chapter.getProperty(CHAPTER_ID).toString());
//					obj2.put("P_Title_ID", chapter.getProperty(CHAPTER_TITLE_ID).toString());
//					obj2.put("S_Title_ID", chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID).toString());
//					obj2.put("P_Lang", chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode().getProperty(LANG_NAME).toString());
//					obj2.put("P_Author_FullName", chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode().getProperty(FULL_NAME).toString());
//					obj2.put("P_Author", chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode().getProperty(USER_ID).toString());
//					obj2.put("P_Feature_Image",chapter.getProperty(CHAPTER_FEAT_IMAGE));
//					chapterArray.put(obj2);
					i++;
				}
				JSONObject chaptersData = new JSONObject();
				chaptersData.put("tp", 3);
				chaptersData.put("data", chapterArray);
				chaptersData.put("ftp", "SC");
				
				i = 0;
				JSONArray anthologyArray = new JSONArray();
				while(anthologyItr.hasNext() && i < 6)
				{
					anthologyArray.put(getJSONForAnthologies(anthologyItr.next(),s_user));
//					JSONObject obj2 = new JSONObject();
//					Node anthology = anthologyItr.next();
//					obj2.put("P_Title", anthology.getProperty(ANTHOLOGY_TITLE).toString()); // lang & autho
//					obj2.put("P_Id", anthology.getProperty(ANTHOLOGY_ID).toString());
//					obj2.put("P_Title_ID", anthology.getProperty(ANTHOLOGY_TITLE_ID).toString());
//					obj2.put("P_Lang", anthology.getSingleRelationship(ANTHOLOGY_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode().getProperty(LANG_NAME).toString());
//					obj2.put("P_Author_FullName", anthology.getSingleRelationship(USER_STARTED_ANTHOLOGY, Direction.INCOMING).getStartNode().getProperty(FULL_NAME).toString());
//					obj2.put("P_Author", anthology.getSingleRelationship(USER_STARTED_ANTHOLOGY, Direction.INCOMING).getStartNode().getProperty(USER_ID).toString());
//					obj2.put("P_Feature_Image",anthology.getProperty(ANTHOLOGY_FEAT_IMG));
//					anthologyArray.put(obj2);
					i++;
				}
				JSONObject anthologyData = new JSONObject();
				anthologyData.put("tp", 4);
				anthologyData.put("data", anthologyArray);
				anthologyData.put("ftp", "SAN");
				
				i = 0;
				JSONArray nanostoryArray = new JSONArray();
				while(nanostoryItr.hasNext() && i < 6)
				{
					nanostoryArray.put(getJSONForNanostory(nanostoryItr.next(),s_user));
					
//					JSONObject obj2 = new JSONObject();
//					Node nanostory = nanostoryItr.next();
//					obj2.put("P_Id", nanostory.getProperty(NANOSTORY_ID).toString());
//					obj2.put("P_Lang", nanostory.getSingleRelationship(NANOSTORY_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode().getProperty(LANG_NAME).toString());
//					obj2.put("P_Author_FullName", nanostory.getSingleRelationship(USER_WRITTEN_A_NANOSTORY, Direction.INCOMING).getStartNode().getProperty(FULL_NAME).toString());
//					obj2.put("P_Author", nanostory.getSingleRelationship(USER_WRITTEN_A_NANOSTORY, Direction.INCOMING).getStartNode().getProperty(USER_ID).toString());
//					obj2.put("P_Feature_Image",nanostory.getProperty(NANOSTORY_FEAT_IMG));
//					nanostoryArray.put(obj2);
					i++;
				}
				JSONObject nanostoryData = new JSONObject();
				nanostoryData.put("tp", 5);
				nanostoryData.put("data", nanostoryArray);
				nanostoryData.put("ftp", "SNS");
				
				jsonArray.put(usersData);
				jsonArray.put(seriesData);
				jsonArray.put(chaptersData);
				jsonArray.put(anthologyData);
				jsonArray.put(nanostoryData);
				
			}
			else if(tp == 1) // users
			{
				userItr = search_index.query(SEARCH_USER, "*"+query.toLowerCase()+"*").iterator();
//				Node s_user = user_index.get(USER_ID, user_id).getSingle();
				
				int c = 0;
				while(c < prev_cnt && userItr.hasNext())
				{
					c++;
					userItr.next();
				}
				c = 0;
				while(userItr.hasNext() && c < count)
				{
					c++;
					jsonArray.put(getJSONForUser(userItr.next(),s_user)); 
				}
			}
			else if(tp == 2) // series
			{
				seriesItr = search_index.query(SEARCH_SERIES, "*"+query.toLowerCase()+"*").iterator();
//				Node s_user = user_index.get(USER_ID, user_id).getSingle();
				
				int c = 0;
				while(c < prev_cnt && seriesItr.hasNext())
				{
					c++;
					seriesItr.next();
				}
				c = 0;
				while(seriesItr.hasNext() && c < count)
				{
					Node series = seriesItr.next();
					// no short stories
					if(!series.getProperty(SERIES_TYPE).toString().equals("2"))
						continue;
					c++;
					jsonArray.put(getJSONForSeries(series,s_user)); 
				}
			}
			else if(tp == 3) // chapters
			{
				chapterItr = search_index.query(SEARCH_CHAPTER, "*"+query.toLowerCase()+"*").iterator();
//				Node s_user = user_index.get(USER_ID, user_id).getSingle();

				int c = 0;
				while(c < prev_cnt && chapterItr.hasNext())
				{
					c++;
					chapterItr.next();
				}
				c = 0;
				while(chapterItr.hasNext() && c < count)
				{
					c++;
					jsonArray.put(getJSONForChapter(chapterItr.next(),s_user)); 
				}
			}
			else if(tp == 4) // Anthologies
			{
				anthologyItr = search_index.query(SEARCH_ANTHOLOGY, "*"+query.toLowerCase()+"*").iterator();
//				Node s_user = user_index.get(USER_ID, user_id).getSingle();

				int c = 0;
				while(c < prev_cnt && anthologyItr.hasNext())
				{
					c++;
					anthologyItr.next();
				}
				c = 0;
				while(anthologyItr.hasNext() && c < count)
				{
					c++;
					jsonArray.put(getJSONForAnthologies(anthologyItr.next(),s_user)); 
				}
			}
			else if(tp == 5) // Nano Stories
			{
				nanostoryItr = search_index.query(SEARCH_NANOSTORY, "*"+query.toLowerCase()+"*").iterator();
//				Node s_user = user_index.get(USER_ID, user_id).getSingle();

				int c = 0;
				while(c < prev_cnt && nanostoryItr.hasNext())
				{
					c++;
					nanostoryItr.next();
				}
				c = 0;
				while(nanostoryItr.hasNext() && c < count)
				{
					c++;
					jsonArray.put(getJSONForNanostory(nanostoryItr.next(),s_user)); 
				}
			}			
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ search()");
			System.out.println("Something went wrong, while returning search  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ search()");
			System.out.println("Something went wrong, while returning search  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();	
	}

	@Override
	public String get_chapter_view_suggestions(String chapter_id, String user_id, int count)
			throws TException {
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);

			if(chapter_id == null || chapter_id.length() == 0)
				throw new KahaniyaCustomException("Empty value receieved for the parameter chapter_id");
			if(user_id == null )
				user_id = "";

			Index<Node> user_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> chapter_index = graphDb.index().forNodes(CHAPTER_ID_INDEX);

			Node user = user_index.get(USER_ID, user_id).getSingle();
			Node chapter = chapter_index.get(CHAPTER_ID, chapter_id).getSingle();
			Node lang = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode();
			
			Node series = chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
			Node author = chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
			Node genre = series.getSingleRelationship(SERIES_BELONGS_TO_GENRE, Direction.OUTGOING).getEndNode();
			int i = 0;
			
			LinkedList<Node> suggested_chapters = new LinkedList<Node>();
			
			//get requested count of chapters from the same series
			//skip the same chapter, user viewed chapter, user written chapter, author deleted chapter
			for(Relationship rel : series.getRelationships(CHAPTER_BELONGS_TO_SERIES))
			{
				if(i==count)
					break;
				Node s_chapter = rel.getStartNode();
				if(chapter.equals(s_chapter))
					break;
				Node auth = s_chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
				if(auth.equals(user) || isRelationExistsBetween(USER_VIEWED_A_CHAPTER, user, s_chapter))
					continue;
				if(auth.hasProperty(IS_DELETED) && auth.getProperty(IS_DELETED).toString().equals("1"))
					continue;
				
				i++;
				suggested_chapters.addLast(s_chapter);
			}
			
			//based on author, skip this if author and viewr is same (i.e., no need to send his own writings as suggestions)
			//skip user viewed chapter
			if(i<count && !author.equals(user) && author.hasProperty(IS_DELETED) && author.getProperty(IS_DELETED).toString().equals("1"))
				for(Relationship rel: author.getRelationships(USER_WRITTEN_A_CHAPTER))
				{
					if(i==count)
						break;
					Node s_chapter = rel.getEndNode();
					//skip if it belongs to other lang
					if(!lang.equals(s_chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode()))
						continue;
					if(isRelationExistsBetween(USER_VIEWED_A_CHAPTER, user, s_chapter))
						continue;
					i++;
					suggested_chapters.addLast(s_chapter);
				}
			Collections.sort(suggested_chapters, TimeCreatedComparatorForNodes);
			
			//get genre based series and find suggestions same as above
			if(i<count)
				for(Relationship rel: genre.getRelationships(SERIES_BELONGS_TO_GENRE))
				{
					if(i==count)
						break;
					for(Relationship rel2 : rel.getStartNode().getRelationships(CHAPTER_BELONGS_TO_SERIES))
					{
						if(i==count)
							break;
						Node s_chapter = rel2.getStartNode();
						if(chapter.equals(s_chapter))
							break;
						//skip if it belongs to other lang
						if(!lang.equals(s_chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode()))
							continue;
						Node auth = s_chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
						if(auth.equals(user) || isRelationExistsBetween(USER_VIEWED_A_CHAPTER, user, s_chapter))
							continue;
						if(auth.hasProperty(IS_DELETED) && auth.getProperty(IS_DELETED).toString().equals("1"))
							continue;
						
						i++;
						suggested_chapters.addLast(s_chapter);
						break;
					}
				}

			//still unable to find, suggestions, simply list all the chapters which are not viewed
			if(i<count)
			{
				ResourceIterator<Node> allChapters = chapter_index.query(CHAPTER_ID, "*").iterator();
				while(allChapters.hasNext() && i < count)
				{
					Node s_chapter = allChapters.next();
					if(chapter.equals(s_chapter))
						break;
					//skip if it belongs to other lang
					if(!lang.equals(s_chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getSingleRelationship(SERIES_BELONGS_TO_LANGUAGE, Direction.OUTGOING).getEndNode()))
						continue;
					Node auth = s_chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
					if(auth.equals(user) || isRelationExistsBetween(USER_VIEWED_A_CHAPTER, user, s_chapter))
						continue;
					if(auth.hasProperty(IS_DELETED) && auth.getProperty(IS_DELETED).toString().equals("1"))
						continue;
					
					i++;
					suggested_chapters.addLast(s_chapter);
				}
			}
			
			for(Node s_chapter : suggested_chapters)
			{
				jsonArray.put(getJSONForChapter(s_chapter, null));
//				Node chapter_author = s_chapter.getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING).getStartNode();
//				JSONObject jsonObject = new JSONObject();
//				jsonObject.put("P_ID", s_chapter.getProperty(CHAPTER_ID).toString());
//				jsonObject.put("P_Title_ID", s_chapter.getProperty(CHAPTER_TITLE_ID).toString());
//				jsonObject.put("P_Title", s_chapter.getProperty(CHAPTER_TITLE).toString());
//				jsonObject.put("P_Author_FullName", chapter_author.getProperty(FULL_NAME));
//				jsonObject.put("P_Author", chapter_author.getProperty(USER_ID));
//				
//				
//				JSONObject seriesJSON = new JSONObject();
//				seriesJSON.put("Series_Id", s_chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_ID));
//				seriesJSON.put("Series_Tid", s_chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE_ID));
//				seriesJSON.put("Series_Typ", s_chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TYPE));
//				seriesJSON.put("Series_Ttl", s_chapter.getSingleRelationship(CHAPTER_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode().getProperty(SERIES_TITLE));
//				
//				jsonObject.put("Series_Info", seriesJSON);
//				jsonArray.put(jsonObject);
			}
			
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapter_view_suggestions()");
			System.out.println("Something went wrong, while returning chapter view suggestions  :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_chapter_view_suggestions()");
			System.out.println("Something went wrong, while returning chapter view suggestions  :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();	
	}

	
	@Override
	public String create_or_edit_contest(String contest_id, String user_id,
			String title, String title_id, String summary, String description,
			String feat_image, String writing_style, String award_info,
			String theme_info, String partners_info, int start_date,
			int end_date, String language, int price, int mode,
			int time_created, int contest_type, int is_open, int is_edit) throws TException {
		if(is_edit == 0)
			return create_contest(contest_id, user_id, title, title_id, summary, description, feat_image, writing_style, award_info, theme_info, partners_info, start_date, end_date, language, price, mode, time_created, contest_type, is_open);
		else if(is_edit == 1)
			return edit_contest(contest_id, user_id, title, title_id, summary, description, feat_image, writing_style, award_info, theme_info, partners_info, start_date, end_date, language, price, mode, time_created, contest_type, is_open);
		else return "false";		

	}

	private String create_contest(String contest_id, String user_id,
			String title, String title_id, String summary, String description,
			String feat_image, String writing_style, String award_info, String theme_info,
			String partners_info, int start_date, int end_date,
			String language, int price, int mode, int time_created, int contest_type, int is_open) {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(writing_style == null || writing_style.length() == 0)
				writing_style = "prose";
			else
				writing_style = writing_style.toLowerCase();
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(title_id == null || title_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title_id");
			if(contest_id == null || contest_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param contest_id");
			if(title == null || title.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title");
			if(language == null || language.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param language");
			if(feat_image == null)
				feat_image = "";
			if(summary == null)
				summary = "";
			if(description == null)
				description = "";
			if(award_info == null)
				award_info = "";
			if(theme_info == null)
				theme_info = "";
			if(partners_info == null)
				partners_info = "";


			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			Index<Node> contestTitleId_index = graphDb.index().forNodes(CONTEST_TITLE_ID_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Node userNode = userId_index.get(USER_ID,user_id).getSingle();
			if(userNode == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			if(contestId_index.get(CONTEST_ID,contest_id).getSingle()!=null)
				throw new KahaniyaCustomException("Contest already exists with given id : "+contest_id);
			
			if(contestTitleId_index.get(CONTEST_TITLE_ID,title_id.toLowerCase()).getSingle()!=null)
				throw new KahaniyaCustomException("Contest already exists with given title id : "+title_id);
			
			System.out.println("contest visibility - "+ is_open);
								
			Node contest_node = Contest(contest_id, title, title_id, summary, description, feat_image, award_info, theme_info, partners_info, start_date, end_date, language, price, mode, time_created, contest_type, is_open);  // Creating a new contest node
			if(contest_node == null)
				throw new KahaniyaCustomException("Something went wrong, while creating contest ");

			//create relationship with user
			createRelation(USER_STARTED_CONTEST, userNode, contest_node, time_created);
			
			Node writingStyleNode = getWritingStyleNode(writing_style);
			createRelation(CONTEST_BELONGS_TO_WRITING_STYLE, contest_node, writingStyleNode);
				
			//Indexing newly created contest node
			contestId_index.add(contest_node, CONTEST_ID, contest_id);
			contestTitleId_index.add(contest_node, CONTEST_TITLE_ID, title_id.toLowerCase());
			
//			search_index.remove(contest_node);
			search_index.add(contest_node, SEARCH_CONTEST, title +""+ title_id.toLowerCase());
			
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_contest()");
			System.out.println("Something went wrong, while creating contest from create_contest  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ create_contest()");
			System.out.println("Something went wrong, while creating contest from create_contest  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;

	}

	private String edit_contest(String contest_id, String user_id,
			String title, String title_id, String summary, String description,
			String feat_image, String writing_style, String award_info, String theme_info,
			String partners_info, int start_date, int end_date,
			String language, int price, int mode, int time_created, int type, int is_open) {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(writing_style == null || writing_style.length() == 0)
				writing_style = "prose";
			else
				writing_style = writing_style.toLowerCase();
			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param user_id");
			if(title_id == null || title_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title_id");
			if(contest_id == null || contest_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param contest_id");
			if(title == null || title.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param title");
			if(language == null || language.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param language");
			if(feat_image == null)
				feat_image = "";
			if(summary == null)
				summary = "";
			if(description == null)
				description = "";
			if(award_info == null)
				award_info = "";
			if(theme_info == null)
				theme_info = "";
			if(partners_info == null)
				partners_info = "";


			aquireWriteLock(tx);
			Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			Index<Node> search_index = graphDb.index().forNodes(SEARCH_INDEX);
			
			Node userNode = userId_index.get(USER_ID,user_id).getSingle();
			if(userNode == null)
				throw new KahaniyaCustomException("User doesnot exists with given id : "+user_id);
			Node contestNode = contestId_index.get(CONTEST_ID,contest_id).getSingle();
			if(contestNode == null)
				throw new KahaniyaCustomException("Contest doesnot exists with given id : "+contest_id);

			Node writingStyleNode = getWritingStyleNode(writing_style);
			if(!isRelationExistsBetween(CONTEST_BELONGS_TO_WRITING_STYLE, contestNode, writingStyleNode))
			{
				Iterator<Relationship> styles = contestNode.getRelationships(CONTEST_BELONGS_TO_WRITING_STYLE).iterator();
				while(styles.hasNext())
					styles.next().delete();
				createRelation(CONTEST_BELONGS_TO_WRITING_STYLE, contestNode, writingStyleNode);
			}

			contestNode.setProperty(CONTEST_TITLE, title);
			contestNode.setProperty(CONTEST_SUMMARY, summary);
			contestNode.setProperty(CONTEST_DESCRIPTION, description);
			contestNode.setProperty(CONTEST_FEAT_IMAGE, feat_image);
			contestNode.setProperty(CONTEST_AWARD_INFO, award_info);
			contestNode.setProperty(CONTEST_THEME_INFO, theme_info);
			contestNode.setProperty(CONTEST_PARTNERS_INFO, partners_info);
			contestNode.setProperty(CONTEST_START_DATE, start_date);
			contestNode.setProperty(CONTEST_END_DATE, end_date);
			contestNode.setProperty(CONTEST_LANGUAGE, language);
			contestNode.setProperty(CONTEST_PRICE, price);
			contestNode.setProperty(CONTEST_TYPE, type);
			contestNode.setProperty(CONTEST_VISIBILTY, is_open);
			contestNode.setProperty(CONTEST_MODE, mode);
			
			search_index.remove(contestNode);
			search_index.add(contestNode, SEARCH_CONTEST, title +""+ title_id.toLowerCase());
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_contest()");
			System.out.println("Something went wrong, while editing contest from edit_contest  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ edit_contest()");
			System.out.println("Something went wrong, while editing contest from edit_contest  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;

	}

	@Override
	public String delete_contest(String contest_id) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			
			if(contest_id == null || contest_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the parameter contest_id");

			Index<Node> contestId_index = graphDb.index().forNodes(CONTEST_ID_INDEX);
			Index<Node> contestTitleId_index = graphDb.index().forNodes(CONTEST_TITLE_ID_INDEX);
			
			Node contest = contestId_index.get(CONTEST_ID,contest_id).getSingle();
			
			if(contest == null)
				throw new KahaniyaCustomException("Contest doesnot exists with given id : "+contest_id);
			
			deleteContestRelationships(contest);

			contestId_index.remove(contest);
			contestTitleId_index.remove(contest);
			contest.delete();
			
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_contest()");
			System.out.println("Something went wrong, while deleting contest from delete_contest  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ delete_contest()");
			System.out.println("Something went wrong, while deleting contest from delete_contest  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;	
	}

	@Override
	public String notifications_count(String user_id)
			throws TException {
		int count = 0;
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);

			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Empty value receieved for the parameter user_id");
			
			Index<Node> user_index = graphDb.index().forNodes(USER_ID_INDEX);
			Node user = user_index.get(USER_ID, user_id).getSingle();
			if(user == null )
				throw new KahaniyaCustomException("User doesnot exists with the given id");

			int last_seen = 0;
			if(user.hasProperty(NTFS_LAST_SEEN))
				last_seen = Integer.parseInt(user.getProperty(NTFS_LAST_SEEN).toString());
			LinkedList<Relationship> discovery_rels = new LinkedList<Relationship>();

			if(user.hasRelationship(USER_FOLLOW_USER, Direction.INCOMING))
			{
				Iterator<Relationship> itr = user.getRelationships(USER_FOLLOW_USER, Direction.INCOMING).iterator();
				LinkedList<Relationship> list = new LinkedList<Relationship>();
				while(itr.hasNext())
				{
					Relationship t_rel = itr.next();
					if(!user.equals(t_rel.getStartNode()))
						list.addLast(t_rel);
				}
				Collections.sort(list, TimeCreatedComparatorForRelationships);
				if(list.size() > 0)
					discovery_rels.addLast(list.getFirst());
			}
			
			if(user.hasRelationship(USER_FOLLOW_USER, Direction.OUTGOING))
			{
				Iterator<Relationship> itr = user.getRelationships(USER_FOLLOW_USER, Direction.OUTGOING).iterator();
				while(itr.hasNext())
				{
					Node user_following = itr.next().getEndNode();
					Iterator<Relationship> startedSeries = user_following.getRelationships(USER_STARTED_SERIES, Direction.OUTGOING).iterator();
					while(startedSeries.hasNext())
					{
						Relationship temp_rel = startedSeries.next();
						Node series = temp_rel.getEndNode();
						//prepare json only for web series
						if(series.getProperty(SERIES_TYPE).toString().equals("2"))
							discovery_rels.addLast(temp_rel);
					}
					
					Iterator<Relationship> writtenChapters = user_following.getRelationships(USER_WRITTEN_A_CHAPTER, Direction.OUTGOING).iterator();
					while(writtenChapters.hasNext())
					{
						discovery_rels.addLast(writtenChapters.next());
					}
					
				}
			}
			
			if(user.hasRelationship(USER_SUBSCRIBED_TO_SERIES, Direction.OUTGOING))
			{
				Iterator<Relationship> itr = user.getRelationships(USER_SUBSCRIBED_TO_SERIES, Direction.OUTGOING).iterator();
				while(itr.hasNext())
				{
					Node subscribedSeries = itr.next().getEndNode();
					//It should be web series & should not follow the author
					if(!isRelationExistsBetween(USER_FOLLOW_USER, user, subscribedSeries.getSingleRelationship(USER_STARTED_SERIES, Direction.INCOMING).getStartNode()) 
							&& subscribedSeries.getProperty(SERIES_TYPE).toString().equals("2"))
					{
						
						Iterator<Relationship> chaptersFromSubSeries = subscribedSeries.getRelationships(CHAPTER_BELONGS_TO_SERIES, Direction.INCOMING).iterator();
						while(chaptersFromSubSeries.hasNext())
						{
							discovery_rels.addLast(chaptersFromSubSeries.next());
						}					
					}
				}
			}

			Iterator<Relationship> startedSeries = user.getRelationships(USER_STARTED_SERIES, Direction.OUTGOING).iterator();
			while(startedSeries.hasNext())
			{
				Node series = startedSeries.next().getEndNode();
				if(series.getProperty(SERIES_TYPE).toString().equals("2") && series.hasRelationship(USER_SUBSCRIBED_TO_SERIES))
				{
					//get the latest subscribed user rel
					Iterator<Relationship> itr = series.getRelationships(USER_SUBSCRIBED_TO_SERIES, Direction.INCOMING).iterator();
					LinkedList<Relationship> list = new LinkedList<Relationship>();
					while(itr.hasNext())
					{
						Relationship t_rel = itr.next();
						if(!user.equals(t_rel.getStartNode()))
							list.addLast(t_rel);
					}
					Collections.sort(list, TimeCreatedComparatorForRelationships);
					if(list.size() > 0)
						discovery_rels.addLast(list.getFirst());
				}
				
				if(series.getProperty(SERIES_TYPE).toString().equals("2") && series.hasRelationship(REVIEW_BELONGS_TO_SERIES))
				{
					//get the latest subscribed user rel
					Iterator<Relationship> itr = series.getRelationships(REVIEW_BELONGS_TO_SERIES, Direction.INCOMING).iterator();
					LinkedList<Relationship> list = new LinkedList<Relationship>();
					while(itr.hasNext())
					{
						Relationship t_rel = itr.next();
						if(!user.equals(t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_REVIEW, Direction.INCOMING).getStartNode()))
							list.addLast(t_rel);
					}
					Collections.sort(list, TimeCreatedComparatorForRelationships);
					if(list.size() > 0)
						discovery_rels.addLast(list.getFirst());
				}
			}
			
			Iterator<Relationship> writtenChapters = user.getRelationships(USER_WRITTEN_A_CHAPTER, Direction.OUTGOING).iterator();
			while(writtenChapters.hasNext())
			{
				Node chapter = writtenChapters.next().getEndNode();
				if(chapter.hasRelationship(COMMENT_WRITTEN_ON_CHAPTER))
				{
					//get the latest subscribed user rel
					Iterator<Relationship> itr = chapter.getRelationships(COMMENT_WRITTEN_ON_CHAPTER, Direction.INCOMING).iterator();
					LinkedList<Relationship> list = new LinkedList<Relationship>();
					while(itr.hasNext())
					{
						Relationship t_rel = itr.next();
						if(!user.equals(t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING).getStartNode()))
							list.addLast(t_rel);
					}
					Collections.sort(list, TimeCreatedComparatorForRelationships);
					if(list.size() > 0)
						discovery_rels.addLast(list.getFirst());
				}
				
				if(chapter.hasRelationship(USER_FAV_CHAPTER))
				{
					//get the latest subscribed user rel
					Iterator<Relationship> itr = chapter.getRelationships(USER_FAV_CHAPTER, Direction.INCOMING).iterator();
					LinkedList<Relationship> list = new LinkedList<Relationship>();
					while(itr.hasNext())
					{
						Relationship t_rel = itr.next();
						if(!user.equals(t_rel.getStartNode()))
							list.addLast(t_rel);
					}
					Collections.sort(list, TimeCreatedComparatorForRelationships);
					if(list.size() > 0)
						discovery_rels.addLast(list.getFirst());
				}
				
				if(chapter.hasRelationship(USER_RATED_A_CHAPTER))
				{
					//get the latest subscribed user rel
					Iterator<Relationship> itr = chapter.getRelationships(USER_RATED_A_CHAPTER, Direction.INCOMING).iterator();
					LinkedList<Relationship> list = new LinkedList<Relationship>();
					while(itr.hasNext())
					{
						Relationship t_rel = itr.next();
						if(!user.equals(t_rel.getStartNode()))
							list.addLast(t_rel);
					}
					Collections.sort(list, TimeCreatedComparatorForRelationships);
					if(list.size() > 0)
						discovery_rels.addLast(list.getFirst());
				}
			}	
			
			for(Relationship rel : discovery_rels)
			{
				if(rel.hasProperty(TIME_CREATED) && Integer.parseInt(rel.getProperty(TIME_CREATED).toString()) > last_seen)
					count++;
			}
			
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ notifications_count()");
			System.out.println("Something went wrong, while returning notification count   :"+ex.getMessage());
//				ex.printStackTrace();
			count= 0;
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ notifications_count()");
			System.out.println("Something went wrong, while returning notification count   :"+ex.getMessage());
			ex.printStackTrace();
			count = 0;
		}
		return String.valueOf(count);	

	}


	@Override
	public String notifications_feed(String user_id, int prev_cnt, int count)
			throws TException {
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);

			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Empty value receieved for the parameter user_id");
			
			Index<Node> user_index = graphDb.index().forNodes(USER_ID_INDEX);
			Node user = user_index.get(USER_ID, user_id).getSingle();
			if(user == null )
				throw new KahaniyaCustomException("User doesnot exists with the given id");
			
			int last_seen = (int)(System.currentTimeMillis()/1000);
			user.setProperty(NTFS_LAST_SEEN, last_seen);
			
			LinkedList<Relationship> discovery_rels = new LinkedList<Relationship>();

			if(user.hasRelationship(USER_FOLLOW_USER, Direction.INCOMING))
			{
				Iterator<Relationship> itr = user.getRelationships(USER_FOLLOW_USER, Direction.INCOMING).iterator();
				LinkedList<Relationship> list = new LinkedList<Relationship>();
				while(itr.hasNext())
				{
					Relationship t_rel = itr.next();
					if(!user.equals(t_rel.getStartNode()))
						list.addLast(t_rel);
				}
				Collections.sort(list, TimeCreatedComparatorForRelationships);
				if(list.size() > 0)
					discovery_rels.addLast(list.getFirst());
			}
			
			//Users you are following
			if(user.hasRelationship(USER_FOLLOW_USER, Direction.OUTGOING))
			{
				Iterator<Relationship> itr = user.getRelationships(USER_FOLLOW_USER, Direction.OUTGOING).iterator();
				while(itr.hasNext())
				{
					Node user_following = itr.next().getEndNode();
					Iterator<Relationship> startedSeries = user_following.getRelationships(USER_STARTED_SERIES, Direction.OUTGOING).iterator();
					while(startedSeries.hasNext())
					{
						Relationship temp_rel = startedSeries.next();
						Node series = temp_rel.getEndNode();
						//prepare json only for web series
						if(series.getProperty(SERIES_TYPE).toString().equals("2"))
							discovery_rels.addLast(temp_rel);
					}
					
					Iterator<Relationship> writtenChapters = user_following.getRelationships(USER_WRITTEN_A_CHAPTER, Direction.OUTGOING).iterator();
					while(writtenChapters.hasNext())
					{
						discovery_rels.addLast(writtenChapters.next());
					}
					
				}
			}
			
			if(user.hasRelationship(USER_SUBSCRIBED_TO_SERIES, Direction.OUTGOING))
			{
				Iterator<Relationship> itr = user.getRelationships(USER_SUBSCRIBED_TO_SERIES, Direction.OUTGOING).iterator();
				while(itr.hasNext())
				{
					Node subscribedSeries = itr.next().getEndNode();
					//It should be web series & should not follow the author
					if(!isRelationExistsBetween(USER_FOLLOW_USER, user, subscribedSeries.getSingleRelationship(USER_STARTED_SERIES, Direction.INCOMING).getStartNode()) 
							&& subscribedSeries.getProperty(SERIES_TYPE).toString().equals("2"))
					{
						
						Iterator<Relationship> chaptersFromSubSeries = subscribedSeries.getRelationships(CHAPTER_BELONGS_TO_SERIES, Direction.INCOMING).iterator();
						while(chaptersFromSubSeries.hasNext())
						{
							discovery_rels.addLast(chaptersFromSubSeries.next().getStartNode().getSingleRelationship(USER_WRITTEN_A_CHAPTER, Direction.INCOMING));
						}					
					}
				}
			}
			
			Iterator<Relationship> startedSeries = user.getRelationships(USER_STARTED_SERIES, Direction.OUTGOING).iterator();
			while(startedSeries.hasNext())
			{
				Node series = startedSeries.next().getEndNode();
				if(series.getProperty(SERIES_TYPE).toString().equals("2") && series.hasRelationship(USER_SUBSCRIBED_TO_SERIES))
				{
					//get the latest subscribed user rel
					Iterator<Relationship> itr = series.getRelationships(USER_SUBSCRIBED_TO_SERIES, Direction.INCOMING).iterator();
					LinkedList<Relationship> list = new LinkedList<Relationship>();
					while(itr.hasNext())
					{
						Relationship t_rel = itr.next();
						if(!user.equals(t_rel.getStartNode()))
							list.addLast(t_rel);
					}
					Collections.sort(list, TimeCreatedComparatorForRelationships);
					if(list.size() > 0)
						discovery_rels.addLast(list.getFirst());
				}
				
				if(series.getProperty(SERIES_TYPE).toString().equals("2") && series.hasRelationship(REVIEW_BELONGS_TO_SERIES))
				{
					//get the latest subscribed user rel
					Iterator<Relationship> itr = series.getRelationships(REVIEW_BELONGS_TO_SERIES, Direction.INCOMING).iterator();
					LinkedList<Relationship> list = new LinkedList<Relationship>();
					while(itr.hasNext())
					{
						Relationship t_rel = itr.next();
						if(!user.equals(t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_REVIEW, Direction.INCOMING).getStartNode()))
							list.addLast(t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_REVIEW, Direction.INCOMING));
					}
					Collections.sort(list, TimeCreatedComparatorForRelationships);
					if(list.size() > 0)
						discovery_rels.addLast(list.getFirst());
				}
			}
			
			Iterator<Relationship> writtenChapters = user.getRelationships(USER_WRITTEN_A_CHAPTER, Direction.OUTGOING).iterator();
			while(writtenChapters.hasNext())
			{
				Node chapter = writtenChapters.next().getEndNode();
				if(chapter.hasRelationship(COMMENT_WRITTEN_ON_CHAPTER))
				{
					//get the latest subscribed user rel
					Iterator<Relationship> itr = chapter.getRelationships(COMMENT_WRITTEN_ON_CHAPTER, Direction.INCOMING).iterator();
					LinkedList<Relationship> list = new LinkedList<Relationship>();
					while(itr.hasNext())
					{
						Relationship t_rel = itr.next();
						if(!user.equals(t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING).getStartNode()))
							list.addLast(t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING));
					}
					Collections.sort(list, TimeCreatedComparatorForRelationships);
					if(list.size() > 0)
						discovery_rels.addLast(list.getFirst());
				}
				
				if(chapter.hasRelationship(USER_FAV_CHAPTER))
				{
					//get the latest subscribed user rel
					Iterator<Relationship> itr = chapter.getRelationships(USER_FAV_CHAPTER, Direction.INCOMING).iterator();
					LinkedList<Relationship> list = new LinkedList<Relationship>();
					while(itr.hasNext())
					{
						Relationship t_rel = itr.next();
						if(!user.equals(t_rel.getStartNode()))
							list.addLast(t_rel);
					}
					Collections.sort(list, TimeCreatedComparatorForRelationships);
					if(list.size() > 0)
						discovery_rels.addLast(list.getFirst());
				}
				
				if(chapter.hasRelationship(USER_RATED_A_CHAPTER))
				{
					//get the latest subscribed user rel
					Iterator<Relationship> itr = chapter.getRelationships(USER_RATED_A_CHAPTER, Direction.INCOMING).iterator();
					LinkedList<Relationship> list = new LinkedList<Relationship>();
					while(itr.hasNext())
					{
						Relationship t_rel = itr.next();
						if(!user.equals(t_rel.getStartNode()))
							list.addLast(t_rel);
					}
					Collections.sort(list, TimeCreatedComparatorForRelationships);
					if(list.size() > 0)
						discovery_rels.addLast(list.getFirst());
				}
			}	
			
			Collections.sort(discovery_rels, TimeCreatedComparatorForRelationships);
			
			int c = 0;
			for(Relationship rel : discovery_rels)
			{
				
				if(c < prev_cnt)
				{
					c ++;
					continue;
				}
				if(c >= count + prev_cnt)
					break;
				jsonArray.put(getJSONForDiscoveryNotificationRel(rel, user));
				c++;
			}
			
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ notification_feed()");
			System.out.println("Something went wrong, while returning notification feed   :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ notification_feed()");
			System.out.println("Something went wrong, while returning notification feed   :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();	

	}

	@Override
	public String discovery_feed(String user_id, int prev_cnt, int count)
			throws TException {
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);

			if(user_id == null || user_id.length() == 0)
				throw new KahaniyaCustomException("Empty value receieved for the parameter user_id");
			
			Index<Node> user_index = graphDb.index().forNodes(USER_ID_INDEX);
			Node user = user_index.get(USER_ID, user_id).getSingle();
			if(user == null )
				throw new KahaniyaCustomException("User doesnot exists with the given id");
			
//			int i = 0;			
			Iterator<Relationship> followingUsersRel = user.getRelationships(USER_FOLLOW_USER, Direction.OUTGOING).iterator();
			
			LinkedList<Relationship> discovery_rels = new LinkedList<Relationship>();

			LinkedList<Node> followUsers = new LinkedList<Node>();
			LinkedList<Node> readChapters = new LinkedList<Node>();
			LinkedList<Node> purchasedChapters = new LinkedList<Node>();
			LinkedList<Node> subscribedSeries = new LinkedList<Node>();
			LinkedList<Node> ratedChapters = new LinkedList<Node>();
			LinkedList<Node> favChapters = new LinkedList<Node>();
			LinkedList<Node> reviewedSeries = new LinkedList<Node>();
			LinkedList<Node> commentedChapters = new LinkedList<Node>();
			
			while(followingUsersRel.hasNext())				
			{
				Node following_user = followingUsersRel.next().getOtherNode(user);
				Iterator<Relationship> followers = following_user.getRelationships(USER_FOLLOW_USER, Direction.OUTGOING).iterator();
				while(followers.hasNext())
				{
					Relationship rel = followers.next();
					if(!rel.getEndNode().equals(user))
					{
						Node f_user = rel.getEndNode();
						if(!followUsers.contains(f_user))
						{
							followUsers.addLast(f_user);
							//get latest user from followers list
							Iterator<Relationship> itr = f_user.getRelationships(USER_FOLLOW_USER, Direction.INCOMING).iterator();
							LinkedList<Relationship> list = new LinkedList<Relationship>();
							while(itr.hasNext())
							{
								Relationship t_rel = itr.next();
								if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
									list.addLast(t_rel);
							}
							Collections.sort(list, TimeCreatedComparatorForRelationships);
							if(list.size() > 0)
								discovery_rels.addLast(list.getFirst());
						}
					}
				}
/*				Iterator<Relationship> series = following_user.getRelationships(USER_STARTED_SERIES, Direction.OUTGOING).iterator();
				while(series.hasNext())
				{
					Relationship rel = series.next();
					if(rel.getEndNode().getProperty(SERIES_TYPE).toString().equals("2"))
						discovery_rels.addLast(rel);
				}
*/
				Iterator<Relationship> reviews = following_user.getRelationships(USER_WRITTEN_A_REVIEW, Direction.OUTGOING).iterator();
				while(reviews.hasNext())
				{
					Relationship rel = reviews.next();
					if(rel.getEndNode().hasRelationship(REVIEW_BELONGS_TO_SERIES))
					{
						Node r_series = rel.getEndNode().getSingleRelationship(REVIEW_BELONGS_TO_SERIES, Direction.OUTGOING).getEndNode();
						if(!reviewedSeries.contains(r_series))
						{
							reviewedSeries.addLast(r_series);
							//get later user from followers list
							Iterator<Relationship> itr = r_series.getRelationships(REVIEW_BELONGS_TO_SERIES, Direction.INCOMING).iterator();
							LinkedList<Relationship> list = new LinkedList<Relationship>();
							while(itr.hasNext())
							{
								Relationship t_rel = itr.next();
								if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_REVIEW, Direction.INCOMING).getStartNode()))
									list.addLast(t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_REVIEW, Direction.INCOMING));
							}
							Collections.sort(list, TimeCreatedComparatorForRelationships);
							if(list.size() > 0)
								discovery_rels.addLast(list.getFirst());
						}
					}
				}
				Iterator<Relationship> sub_series = following_user.getRelationships(USER_SUBSCRIBED_TO_SERIES, Direction.OUTGOING).iterator();
				while(sub_series.hasNext())
				{
					Relationship rel = sub_series.next();
					Node s_series = rel.getEndNode();
					if(!subscribedSeries.contains(s_series))
					{
						subscribedSeries.addLast(s_series);
						//get latest user from followers list
						Iterator<Relationship> itr = s_series.getRelationships(USER_SUBSCRIBED_TO_SERIES, Direction.INCOMING).iterator();
						LinkedList<Relationship> list = new LinkedList<Relationship>();
						while(itr.hasNext())
						{
							Relationship t_rel = itr.next();
							if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
								list.addLast(t_rel);
						}
						Collections.sort(list, TimeCreatedComparatorForRelationships);
						if(list.size() > 0)
							discovery_rels.addLast(list.getFirst());
					}
				}
				Iterator<Relationship> fav_chapters = following_user.getRelationships(USER_FAV_CHAPTER, Direction.OUTGOING).iterator();
				while(fav_chapters.hasNext())
				{
					Relationship rel = fav_chapters.next();
					Node fav_chapter = rel.getEndNode();
					if(!favChapters.contains(fav_chapter))
					{
						favChapters.addLast(fav_chapter);
						//get latest user from followers list
						Iterator<Relationship> itr = fav_chapter.getRelationships(USER_FAV_CHAPTER, Direction.INCOMING).iterator();
						LinkedList<Relationship> list = new LinkedList<Relationship>();
						while(itr.hasNext())
						{
							Relationship t_rel = itr.next();
							if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
								list.addLast(t_rel);
						}
						Collections.sort(list, TimeCreatedComparatorForRelationships);
						if(list.size() > 0)
							discovery_rels.addLast(list.getFirst());
					}
				}
				Iterator<Relationship> pur_chapters = following_user.getRelationships(USER_PURCHASED_A_CHAPTER, Direction.OUTGOING).iterator();
				while(pur_chapters.hasNext())
				{
					Relationship rel = pur_chapters.next();

					//for every chapter view, there will a purchase with 0 or more coins, so skip 0 coins purchases
					if(Integer.parseInt(rel.getProperty(CHAPTER_PURCHASED_PRICE).toString()) <= 0)
						continue;
						
					Node pur_chapter = rel.getEndNode();
					if(!purchasedChapters.contains(pur_chapter))
					{
						purchasedChapters.addLast(pur_chapter);
						//get latest user from followers list
						Iterator<Relationship> itr = pur_chapter.getRelationships(USER_PURCHASED_A_CHAPTER, Direction.INCOMING).iterator();
						LinkedList<Relationship> list = new LinkedList<Relationship>();
						while(itr.hasNext())
						{
							Relationship t_rel = itr.next();
							if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
								list.addLast(t_rel);
						}
						Collections.sort(list, TimeCreatedComparatorForRelationships);
						if(list.size() > 0)
							discovery_rels.addLast(list.getFirst());
					}
				}
				Iterator<Relationship> rated_chapters = following_user.getRelationships(USER_RATED_A_CHAPTER, Direction.OUTGOING).iterator();
				while(rated_chapters.hasNext())
				{
					Relationship rel = rated_chapters.next();
					Node rated_chapter = rel.getEndNode();
					if(!ratedChapters.contains(rated_chapter))
					{
						ratedChapters.addLast(rated_chapter);
						//get latest user from followers list
						Iterator<Relationship> itr = rated_chapter.getRelationships(USER_RATED_A_CHAPTER, Direction.INCOMING).iterator();
						LinkedList<Relationship> list = new LinkedList<Relationship>();
						while(itr.hasNext())
						{
							Relationship t_rel = itr.next();
							if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
								list.addLast(t_rel);
						}
						Collections.sort(list, TimeCreatedComparatorForRelationships);
						if(list.size() > 0)
							discovery_rels.addLast(list.getFirst());
					}
				}
				Iterator<Relationship> read_chapters = following_user.getRelationships(USER_READ_A_CHAPTER, Direction.OUTGOING).iterator();
				while(read_chapters.hasNext())
				{
					Relationship rel = read_chapters.next();
					Node read_chapter = rel.getEndNode();
					if(!readChapters.contains(read_chapter))
					{
						readChapters.addLast(read_chapter);
						//get latest user from followers list
						Iterator<Relationship> itr = read_chapter.getRelationships(USER_READ_A_CHAPTER, Direction.INCOMING).iterator();
						LinkedList<Relationship> list = new LinkedList<Relationship>();
						while(itr.hasNext())
						{
							Relationship t_rel = itr.next();
							if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode()))
								list.addLast(t_rel);
						}
						Collections.sort(list, TimeCreatedComparatorForRelationships);
						if(list.size() > 0)
							discovery_rels.addLast(list.getFirst());
					}
				}
				Iterator<Relationship> comments = following_user.getRelationships(USER_WRITTEN_A_COMMENT, Direction.OUTGOING).iterator();
				while(comments.hasNext())
				{
					Relationship rel = comments.next();
					if(rel.getEndNode().hasRelationship(COMMENT_WRITTEN_ON_CHAPTER) )
					{
						Node c_chapter = rel.getEndNode().getSingleRelationship(COMMENT_WRITTEN_ON_CHAPTER, Direction.OUTGOING).getEndNode();
						if(!commentedChapters.contains(c_chapter))
						{
							commentedChapters.addLast(c_chapter);
							//get latest user from followers list
							Iterator<Relationship> itr = c_chapter.getRelationships(COMMENT_WRITTEN_ON_CHAPTER, Direction.INCOMING).iterator();
							LinkedList<Relationship> list = new LinkedList<Relationship>();
							while(itr.hasNext())
							{
								Relationship t_rel = itr.next();
								if(isRelationExistsBetween(USER_FOLLOW_USER, user, t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING).getStartNode()))
									list.addLast(t_rel.getStartNode().getSingleRelationship(USER_WRITTEN_A_COMMENT, Direction.INCOMING));
							}
							Collections.sort(list, TimeCreatedComparatorForRelationships);
							if(list.size() > 0)
								discovery_rels.addLast(list.getFirst());
						}
					}
				}

			}
			
			Collections.sort(discovery_rels, TimeCreatedComparatorForRelationships);
			
			int c = 0;
			for(Relationship rel : discovery_rels)
			{
				
				if(c < prev_cnt)
				{
					c ++;
					continue;
				}
				if(c >= count + prev_cnt)
					break;
				jsonArray.put(getJSONForDiscoveryNotificationRel(rel, user));
				c++;
			}
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ discovery_feed()");
			System.out.println("Something went wrong, while returning discovery feed   :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ discovery_feed()");
			System.out.println("Something went wrong, while returning discovery feed   :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();	

	}

	@Override
	public String get_genre_followers(String genre_name)
			throws TException {
		JSONArray jsonArray = new JSONArray();		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);

			if(genre_name == null || genre_name.length() == 0)
				throw new KahaniyaCustomException("Empty value receieved for the parameter genre_name");
						
			Index<Node> genreName_index = graphDb.index().forNodes(GENRE_NAME_INDEX);
			Node genre = genreName_index.get(GENRE_NAME, genre_name.toLowerCase()).getSingle();
			if(genre == null )
				throw new KahaniyaCustomException("Genre doesnot exists with the given genre name");
					
			Iterator<Relationship> followingUsersRel = genre.getRelationships(USER_INTERESTED_GENRE, Direction.INCOMING).iterator();
			
			while(followingUsersRel.hasNext())
			{
				jsonArray.put(new JSONObject().put("userId", followingUsersRel.next().getStartNode().getProperty(USER_ID)));
			}	
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_genre_followers()");
			System.out.println("Something went wrong, while returning genre followers   :"+ex.getMessage());
//				ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_genre_followers()");
			System.out.println("Something went wrong, while returning genre followers   :"+ex.getMessage());
			ex.printStackTrace();
			jsonArray = new JSONArray();
		}
		return jsonArray.toString();	

	}
	
	@Override
	public String update_series_going_status(String series_id) throws TException {
		String res;		
		try(Transaction tx = graphDb.beginTx())
		{
			if(series_id == null || series_id.length() == 0)
				throw new KahaniyaCustomException("Null or empty string receieved for the param series_id");
			
			aquireWriteLock(tx);
			Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
			Index<Node> seriesStatusBaseIndex = graphDb.index().forNodes(LOCK_INDEX);
			
			Node seriesCompletedBaseNode = seriesStatusBaseIndex.get(SERIES_STATUS, SERIES_COMPLETED_BASE_NODE).getSingle();
			if(seriesCompletedBaseNode == null)
				throw new KahaniyaCustomException("Unable to find SERIES_COMPLETED_BASE_NODE");
			Node series_node = seriesId_index.get(SERIES_ID, series_id).getSingle();
			if(series_node == null)
				throw new KahaniyaCustomException("Series doesnot exists with given id : "+series_id);
			
			if(series_node.hasRelationship(SERIES_STARTED_REL_TO_BASE_NODE))
			{
				series_node.getSingleRelationship(SERIES_STARTED_REL_TO_BASE_NODE, Direction.OUTGOING).delete();
				createRelation(SERIES_COMPLETED_REL_TO_BASE_NODE, series_node, seriesCompletedBaseNode);
			}
			else if(series_node.hasRelationship(SERIES_ONGOING_REL_TO_BASE_NODE))
			{
				series_node.getSingleRelationship(SERIES_ONGOING_REL_TO_BASE_NODE, Direction.OUTGOING).delete();
				createRelation(SERIES_COMPLETED_REL_TO_BASE_NODE, series_node, seriesCompletedBaseNode);
			}
						
			res = "true";
			tx.success();

		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ update_series_going_status()");
			System.out.println("Something went wrong, while changing series status to series_completed update_series_going_status  :"+ex.getMessage());
//				ex.printStackTrace();
			res = "false";
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ update_series_going_status()");
			System.out.println("Something went wrong, while changing series status to series_completed update_series_going_status  :"+ex.getMessage());
			ex.printStackTrace();
			res = "false";
		}
		return res;
	}
	
	@Override
	public int get_view_count(String typeId, String type)
			throws TException {		
		try(Transaction tx = graphDb.beginTx())
		{
			aquireWriteLock(tx);

			if(typeId == null || typeId.length() == 0)
				throw new KahaniyaCustomException("Empty value receieved for the parameter typeId");
			if(type == null || type.length() == 0)
				throw new KahaniyaCustomException("Empty value receieved for the parameter type");
			int views_count = 0;
			switch (type) {
			case "A":
				Index<Node> userId_index = graphDb.index().forNodes(USER_ID_INDEX);
				Node user_node = userId_index.get(USER_ID,typeId).getSingle();
				Iterator<Relationship> CHaptersRelsItr = user_node.getRelationships(USER_WRITTEN_A_CHAPTER, Direction.OUTGOING).iterator();
				LinkedList<Relationship> ChaptersRelsList = new LinkedList<Relationship>();
				while(CHaptersRelsItr.hasNext())
					ChaptersRelsList.addLast(CHaptersRelsItr.next());
				for(Relationship rel : ChaptersRelsList)
				{
//					views_count += Integer.parseInt(rel.getEndNode().getProperty(TOTAL_CHAPTER_VIEWS).toString());
					views_count += getAccumulatedChapterViews(rel.getEndNode());					
				}
				return views_count;
			case "C":
				Index<Node> chapterNodeIndex = graphDb.index().forNodes(CHAPTER_ID_INDEX);
				Node chapterNode = chapterNodeIndex.get(CHAPTER_ID,typeId).getSingle();
				if(chapterNode == null)
					throw new KahaniyaCustomException("Chapter doesnot exists with given id : "+typeId);
				return views_count = getAccumulatedChapterViews(chapterNode);									
			case "S":
				Index<Node> seriesId_index = graphDb.index().forNodes(SERIES_ID_INDEX);
				Node series_node = seriesId_index.get(SERIES_ID,typeId).getSingle();
				Iterator<Relationship> ChaptersRelsItr = series_node.getRelationships(CHAPTER_BELONGS_TO_SERIES, Direction.INCOMING).iterator();
				LinkedList<Relationship> CHaptersRelsList = new LinkedList<Relationship>();
				while(ChaptersRelsItr.hasNext())
					CHaptersRelsList.addLast(ChaptersRelsItr.next());
				for(Relationship rel : CHaptersRelsList)
				{
//					views_count += Integer.parseInt(rel.getStartNode().getProperty(TOTAL_CHAPTER_VIEWS).toString());
					views_count += getAccumulatedChapterViews(rel.getStartNode());
				}
				return views_count;
			default:
				break;
			}			
			tx.success();
		}
		catch(KahaniyaCustomException ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_views_count()");
			System.out.println("Something went wrong, while returning views count   :"+ex.getMessage());
//				ex.printStackTrace();
		}
		catch(Exception ex)
		{
			System.out.println(new Date().toString());
			System.out.println("Exception @ get_views_count()");
			System.out.println("Something went wrong, while returning views count   :"+ex.getMessage());
			ex.printStackTrace();
		}
		return 0;	

	}


}
