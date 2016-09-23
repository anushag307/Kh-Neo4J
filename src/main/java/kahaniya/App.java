package kahaniya;

public class App 
{
    public static void main( String[] args )
    {
		System.out.println("23th Sept, 2016 - 08:50 P.M");
		
		Kahaniya kahaniya = new Kahaniya();
		kahaniya.add_neo4j_lock_nodes();
//		kahaniya.add_additional_properties();
//		kahaniya.add_chapter_contest_status_properties();
//		test(kahaniya);
		kahaniya.startThriftServer();

    }
    
    public static void test( Kahaniya k)
    {
    	try{
 /*   		System.out.println(k.create_or_edit_contest("contest10", "one", "contest title 10", "contest-title-id-10", "contest-summary", "", "feat_image", "prose", "", "", "", 100, 1000, "LANG3", 0, 1, 900, 0));
    		System.out.println(k.create_or_edit_contest("contest11", "one", "contest title 11", "contest-title-id-11", "contest-summary", "", "feat_image", "prose", "", "", "", 200, 1100, "LANG3", 0, 1, 900, 0));
    		System.out.println(k.create_or_edit_contest("contest12", "one", "contest title 12", "contest-title-id-12", "contest-summary", "", "feat_image", "prose", "", "", "", 300, 1200, "LANG3", 0, 1, 900, 0));
    		System.out.println(k.create_or_edit_contest("contest13", "one", "contest title 13", "contest-title-id-13", "contest-summary", "", "feat_image", "prose", "", "", "", 400, 1300, "LANG3", 0, 1, 900, 0));
    		System.out.println(k.create_or_edit_contest("contest14", "one", "contest title 14", "contest-title-id-14", "contest-summary", "", "feat_image", "prose", "", "", "", 500, 1400, "LANG3", 0, 1, 900, 0));
			System.out.println(k.get_feed("CN","L","{actv:\"0\"}",0,100,"","","","","",0));
			System.out.println(k.get_feed("CN","L","{actv:\"1\"}",0,100,"","","","","",0));
			System.out.println(k.get_feed("CN","L","{actv-:\"1\"}",0,100,"","","","","",0));
			System.out.println(k.get_feed("CN","L","",0,100,"","","","","",0));

    		k.create_genre("poetry");
    		k.delete_contest("contest2");
    		k.delete_series("cs2");
    		k.delete_series("css2");
    		System.out.println(k.create_or_edit_contest("contest2", "one", "contest title 2", "contest-title-id-2", "contest-summary", "", "feat_image", "prose", "", "", "", 1000, 1050, "LANG3", 0, 1, 900, 0));
    		System.out.println(k.create_or_edit_series("cs2", "one", "cs2", "cs2", "cs2", "feature_image", "GEN2", "LANG2", "keywords", "copyrights", "dd_img", "dd_summary", 1, 102, 0,"prose","contest2"));
    		System.out.println(k.create_or_edit_chapter("cc2", "cs2", "series_type", "two", "cc2", "cc2", "feat_image", 102, 2, 0,"contest2"));
    		System.out.println(k.create_or_edit_series("css2", "n-one", "css2", "css2", "css2", "feature_image", "GEN3", "LANG1", "keywords", "copyrights", "dd_img", "dd_summary", 1, 102, 0,"prose","contest2"));
    		System.out.println(k.create_or_edit_chapter("ccc1", "css2", "series_type", "n-one", "ccc1", "ccc1", "feat_image", 102, 0,0,"contest2"));

    		System.out.println(k.update_chapter_contest_status("cc2","contest2",1));
    		System.out.println(k.update_chapter_contest_status("ccc1","contest2",2));
    		System.out.println(k.get_feed("C","CN","",0,100,"","","","","contest2",0));
			System.out.println(k.get_feed("C","CN","",0,100,"","","","","contest2",1));
			System.out.println(k.get_feed("C","CN","",0,100,"","","","","contest2",2));

    		System.out.println(k.update_chapter_contest_status("cc2","contest2",1));
    		System.out.println(k.update_chapter_contest_status("ccc1","contest2",2));
			System.out.println(k.get_feed("C","CN","",0,100,"","","","","contest2",0));
			System.out.println(k.get_feed("C","CN","",0,100,"","","","","contest2",1));
			System.out.println(k.get_feed("C","CN","",0,100,"","","","","contest2",2));
			
    		System.out.println(k.update_chapter_contest_status("cc2","contest2",2));
    		System.out.println(k.update_chapter_contest_status("ccc1","contest2",2));
			System.out.println(k.get_feed("C","CN","",0,100,"","","","","contest2",0));
			System.out.println(k.get_feed("C","CN","",0,100,"","","","","contest2",1));
			System.out.println(k.get_feed("C","CN","",0,100,"","","","","contest2",2));
*/
/*
    		System.out.println("stats");
    		System.out.println(k.get_stats("U"));
    		System.out.println(k.get_stats("S"));
    		System.out.println(k.get_stats("CH"));
    		System.out.println(k.get_stats("R"));
    		System.out.println(k.get_stats("C"));
    		System.out.println(k.get_stats("L"));
    		System.out.println(k.get_stats("G"));
    		System.out.println(k.delete_series("ss2"));
    		k.create_genre("GEN3");
    		k.create_genre("GEN4");
    		k.create_language("LANG3");
    		k.create_language("LANG4");
    		k.create_user("n-one", "n-one", "n-one", "n-one", "n-one", "n-one","dob","gender","address","bio", "GEN2,GEN3", "LANG3,LANG4", 2, 2, 100);
    		k.create_user("n-two", "n-two", "n-two", "n-two", "n-two", "n-two","dob","gender","address","bio", "GEN1,GEN3", "LANG2,LANG3", 2, 2, 100);
    		k.create_or_edit_series("ss1", "n-one", "ss1", "ss1", "ss1", "feature_image", "GEN4", "LANG4", "keywords", "copyrights", "dd_img", "dd_summary", 1, 101, 0);
    		k.create_or_edit_series("ss2", "n-one", "ss2", "ss2", "ss2", "feature_image", "GEN3", "LANG1", "keywords", "copyrights", "dd_img", "dd_summary", 1, 102, 0);
    		k.create_or_edit_chapter("cc1", "ss2", "series_type", "n-one", "cc1", "cc1", "feat_image", 102, 0, 0);
    		k.create_or_edit_series("ss3", "two", "ss3", "ss3", "ss3", "feature_image", "GEN4", "LANG2", "keywords22", "copyrights", "dd_img", "dd_summary", 1, 101, 0);
    		k.create_or_edit_chapter("c2", "s2", "series_type", "two", "c2", "c2", "feat_image", 102, 2, 0);

    		System.out.println(k.get_feed("C", "L", "", 0, 10, "", "", "",""));
    		System.out.println(k.get_feed("C", "R", "", 0, 10, "", "", "",""));
    		System.out.println(k.get_feed("A","D", "", 0, 10, "", "", "",""));
    		System.out.println(k.get_feed("S", "R", "", 0, 10, "one", "", "",""));
    		System.out.println(k.get_feed("S", "R", "", 0, 10, "two", "", "",""));

    		System.out.println("all items");
    		System.out.println(k.get_all_items("C",0,1000000));
    		System.out.println(k.get_all_items("S",0,1000000));
    		System.out.println(k.get_all_items("A",0,1000000));
    		System.out.println(k.get_all_items("R",0,1000000));
    		System.out.println(k.list_genres());
    		System.out.println(k.list_languages());

    		System.out.println("stats");
    		System.out.println(k.get_stats("U"));
    		System.out.println(k.get_stats("S"));
    		System.out.println(k.get_stats("CH"));
    		System.out.println(k.get_stats("R"));
    		System.out.println(k.get_stats("C"));
    		System.out.println(k.get_stats("L"));
    		System.out.println(k.get_stats("G"));
 
    		System.out.println(k.edit_user_status("one", 3));
    		System.out.println(k.get_all_items("A", 0, 10));
    		System.out.println(k.edit_user_status("two", 1));
    		System.out.println(k.get_all_items("A", 0, 10));
    		System.out.println(k.edit_user_status("three", 2));
    		System.out.println(k.get_all_items("A", 0, 10));
    		System.out.println(k.get_all_items("C", 0, 100));
    		System.out.println(k.get_all_items("S", 0, 100));
*/    		
    		//k.create_genre("GEN1");
    		//k.create_genre("GEN2");
    		//k.create_language("LANG1");
    		//k.create_language("LANG2");
    		//k.create_user("one", "one", "one", "one", "one", "one", "GEN1,GEN2", "LANG1,LANG2", 2, 2, 100);
    		//k.create_user("two", "two", "two", "two", "two", "two", "GEN1,GEN2", "LANG1,LANG2", 2, 2, 100);
    		//k.create_or_edit_series("s1", "one", "s1", "s1", "s1", "feature_image", "GEN1", "LANG2", "keywords", "copyrights", "dd_img", "dd_summary", 1, 101, 0);
    		//k.create_or_edit_series("s2", "one", "s2", "s2", "s2", "feature_image", "GEN2", "LANG2", "keywords", "copyrights", "dd_img", "dd_summary", 1, 102, 0);
    		//k.create_or_edit_chapter("c1", "s1", "series_type", "one", "c1", "c1", "feat_image", 102, 0, 0);
    		//System.out.println(k.get_feed("C", "R", "", 0, 10, "two"));
    		//k.favourite_chapter("c1", "s1", "two", 103);
    		//System.out.println(k.get_feed("S", "SUB", "", 0, 10, "one"));
    		//k.subscribe_series("s1", "one", 105);
    		//System.out.println(k.get_feed("S", "SUB", "", 0, 10, "one"));
    		//k.subscribe_series("s1", "one", 105);
    		//System.out.println(k.get_feed("S", "SUB", "", 0, 10, "one"));
    		//System.out.println(k.get_feed("C", "F", "", 0, 10, "two"));
    		//System.out.println(k.get_feed("", "R", "", 0, 5, "one"));
    		//System.out.println(k.get_feed("", "R", "", 1, 5, "one"));
    		//System.out.println(k.get_feed("", "R", "", 2, 5, "one"));
    		//System.out.println(k.get_feed("", "R", "", 0, 5, "two"));
    		//System.out.println(k.get_feed("", "R", "", 1, 5, "two"));
    		//System.out.println(k.get_feed("", "R", "", 2, 5, "two"));
/*    		System.out.println(k.get_feed("C","G", "", 0, 10, "", "GEN1", ""));
    		System.out.println(k.get_feed("C","G", "", 0, 10, "", "GEN2", ""));
    		System.out.println(k.get_feed("C","LNG", "", 0, 10, "", "", "LANG1"));
    		System.out.println(k.get_feed("C","LNG", "", 0, 10, "", "", "LANG2"));
    		System.out.println(k.get_feed("S","G", "", 0, 10, "", "GEN1", ""));
    		System.out.println(k.get_feed("S","G", "", 0, 10, "", "GEN2", ""));
    		System.out.println(k.get_feed("S","LNG", "", 0, 10, "", "", "LANG1"));
    		System.out.println(k.get_feed("S","LNG", "", 0, 10, "", "", "LANG2")); */
    		//k.create_or_edit_chapter("c3", "s1", "series_type", "one", "c3", "c3", "feat_image", 104, 0, 0);
    		//k.create_or_edit_chapter("c2", "s1", "series_type", "one", "c2", "c2", "feat_image", 103, 0, 0);
    		//k.create_or_edit_chapter("c4", "s1", "series_type", "one", "c4", "c4", "feat_image", 105, 0, 0);
    		
    		//System.out.println(k.get_feed("C", "L", "", 0, 10, "", "", ""));
    		//System.out.println(k.get_feed("A","D", "", 0, 10, "", "", ""));
    		//System.out.println(k.get_feed("S", "R", "", 0, 10, "", "", ""));
    		//System.out.println(k.get_feed("S", "R", "", 0, 10, "one", "", ""));
    		//System.out.println(k.get_feed("S", "R", "", 0, 10, "two", "", ""));
    		//k.create_user("three", "three", "three", "three", "three", "three", "three", "three", "three", "three", "GEN1,GEN2", "LANG1,LANG2", 2, 2, 105);
    		
    	}catch(Exception te)
    	{
    		System.out.println("TException");
    		te.printStackTrace();
    	}
    	System.exit(0);
    } 
}
