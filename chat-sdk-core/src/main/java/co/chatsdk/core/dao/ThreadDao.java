package co.chatsdk.core.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.SqlUtils;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "THREAD".
*/
public class ThreadDao extends AbstractDao<Thread, Long> {

    public static final String TABLENAME = "THREAD";

    /**
     * Properties of entity Thread.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property EntityID = new Property(1, String.class, "entityID", false, "ENTITY_ID");
        public final static Property CreationDate = new Property(2, java.util.Date.class, "creationDate", false, "CREATION_DATE");
        public final static Property Deleted = new Property(3, Boolean.class, "deleted", false, "DELETED");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property Type = new Property(5, Integer.class, "type", false, "TYPE");
        public final static Property ImageUrl = new Property(6, String.class, "imageUrl", false, "IMAGE_URL");
        public final static Property CreatorId = new Property(7, Long.class, "creatorId", false, "CREATOR_ID");
    }

    private DaoSession daoSession;


    public ThreadDao(DaoConfig config) {
        super(config);
    }
    
    public ThreadDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"THREAD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"ENTITY_ID\" TEXT UNIQUE ," + // 1: entityID
                "\"CREATION_DATE\" INTEGER," + // 2: creationDate
                "\"DELETED\" INTEGER," + // 3: deleted
                "\"NAME\" TEXT," + // 4: name
                "\"TYPE\" INTEGER," + // 5: type
                "\"IMAGE_URL\" TEXT," + // 6: imageUrl
                "\"CREATOR_ID\" INTEGER);"); // 7: creatorId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"THREAD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Thread entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String entityID = entity.getEntityID();
        if (entityID != null) {
            stmt.bindString(2, entityID);
        }
 
        java.util.Date creationDate = entity.getCreationDate();
        if (creationDate != null) {
            stmt.bindLong(3, creationDate.getTime());
        }
 
        Boolean deleted = entity.getDeleted();
        if (deleted != null) {
            stmt.bindLong(4, deleted ? 1L: 0L);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(6, type);
        }
 
        String imageUrl = entity.getImageUrl();
        if (imageUrl != null) {
            stmt.bindString(7, imageUrl);
        }
 
        Long creatorId = entity.getCreatorId();
        if (creatorId != null) {
            stmt.bindLong(8, creatorId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Thread entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String entityID = entity.getEntityID();
        if (entityID != null) {
            stmt.bindString(2, entityID);
        }
 
        java.util.Date creationDate = entity.getCreationDate();
        if (creationDate != null) {
            stmt.bindLong(3, creationDate.getTime());
        }
 
        Boolean deleted = entity.getDeleted();
        if (deleted != null) {
            stmt.bindLong(4, deleted ? 1L: 0L);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(6, type);
        }
 
        String imageUrl = entity.getImageUrl();
        if (imageUrl != null) {
            stmt.bindString(7, imageUrl);
        }
 
        Long creatorId = entity.getCreatorId();
        if (creatorId != null) {
            stmt.bindLong(8, creatorId);
        }
    }

    @Override
    protected final void attachEntity(Thread entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Thread readEntity(Cursor cursor, int offset) {
        Thread entity = new Thread( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // entityID
            cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)), // creationDate
            cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // deleted
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // type
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // imageUrl
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7) // creatorId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Thread entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setEntityID(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCreationDate(cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)));
        entity.setDeleted(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setType(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setImageUrl(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCreatorId(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Thread entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Thread entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Thread entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserDao().getAllColumns());
            builder.append(" FROM THREAD T");
            builder.append(" LEFT JOIN USER T0 ON T.\"CREATOR_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Thread loadCurrentDeep(Cursor cursor, boolean lock) {
        Thread entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        User creator = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setCreator(creator);

        return entity;    
    }

    public Thread loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Thread> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Thread> list = new ArrayList<Thread>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Thread> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Thread> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
