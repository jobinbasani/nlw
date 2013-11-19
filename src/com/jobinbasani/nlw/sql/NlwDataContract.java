package com.jobinbasani.nlw.sql;

import android.provider.BaseColumns;

public final class NlwDataContract  {

	public NlwDataContract() {
	}
	
	public static abstract class NlwDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "nlwentry";
        public static final String COLUMN_NAME_NLWNAME = "name";
        public static final String COLUMN_NAME_NLWTEXT = "text";
        public static final String COLUMN_NAME_NLWWIKI = "wiki";
    }

}
