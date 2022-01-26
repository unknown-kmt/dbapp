package dbapp.dbapp;

import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Class for executes custom SQL query's and already prepared ones and gives results
 */
public record DBTalker(DBConnection dbConnection) {
    private static final Logger logger = Logger.getLogger(DBTalker.class.getName());

    public void writeToDB(String query, String... args) throws SQLException, ClassNotFoundException {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement(query);

            for (int i = 1; i <= args.length; i++) { // set all args
                pst.setString(i, args[i]);
            }
            pst.executeUpdate();
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while writing to DB \n" + ex.getMessage());
        }
    }

    public ResultSet readFromDB(String query) {
        ResultSet result = null;

        try {
            Connection conn = dbConnection.connect();
            Statement statement = conn.createStatement();
            result = statement.executeQuery(query);
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return result;
    }

    public String getDiskHaving(String param) {

        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement("""
                    SELECT title, place, audios_on_disk, doc_on_disk, soft_on_disk
                    FROM disksdb.public.disk
                    LEFT JOIN disksdb.public.audio_disk USING(disk_id)
                    LEFT JOIN disksdb.public.document_disk USING(disk_id)
                    LEFT JOIN disksdb.public.film_disk USING(disk_id)
                    LEFT JOIN disksdb.public.software_disk USING(disk_id)
                    WHERE LOWER(title) LIKE ? OR LOWER(place) = ? OR LOWER(audios_on_disk) LIKE ? OR LOWER(doc_on_disk) LIKE ? OR LOWER(soft_on_disk) LIKE ?
                    GROUP BY title, place, audios_on_disk, doc_on_disk, soft_on_disk
                    ORDER BY title
                    """
            );

            for (int i = 1; i <= 5; i++) {
                pst.setString(i, "%" + param.toLowerCase() + "%");
            }

            ResultSet rs = pst.executeQuery();

            return toStringer(rs, "title", "place", "audios_on_disk", "doc_on_disk", "soft_on_disk");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return null;
    }

    public String getDuplicates() {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement("""
                    SELECT title, place, COUNT(title) as duplicates
                    FROM disksdb.public.disk
                    GROUP BY title, place
                    HAVING COUNT(title) > 1
                    """
            );

            ResultSet rs = pst.executeQuery();

            return toStringer(rs, "title", "place", "duplicates");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return null;
    }

    public String getDisksByTitle(String title) {
        try {
            Connection conn = dbConnection.connect();

            PreparedStatement pst = conn.prepareStatement("""
                    SELECT title, place
                    FROM disksdb.public.disk
                    WHERE LOWER(title) LIKE ?
                    GROUP BY title, place
                    ORDER BY title
                    """
            );

            pst.setString(1, "%" + title.toLowerCase() + "%");
            ResultSet rs = pst.executeQuery();

            return toStringer(rs, "title", "place");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return null;
    }


    public String getDisksByPlace(String place) {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement("""
                    SELECT title, place
                    FROM disksdb.public.disk
                    WHERE LOWER(place) LIKE ?
                    GROUP BY title, place
                    ORDER BY title
                    """
            );

            pst.setString(1, "%" + place.toLowerCase() + "%");
            ResultSet rs = pst.executeQuery();

            return toStringer(rs, "title", "place");

        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }
        return null;
    }

    public String getStatsPlaces() {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement(
                    """
                            SELECT place, COUNT(LOWER(place)) as count
                            FROM disksdb.public.disk
                            GROUP BY place
                            ORDER BY COUNT(place) DESC
                            """
            );

            ResultSet rs = pst.executeQuery();

            return toStringer(rs, "place", "count");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }
        return null;
    }

    public String getDisksByParamsSet(String title, String place, String publisher, String mediaType, Double costFrom, Double costTo) {
        try {
            if (title.isEmpty() && place.isEmpty() && publisher.isEmpty() && mediaType.isEmpty() && costFrom == null && costTo == null)
                return "";

            Connection conn = dbConnection.connect();

            String query =
                    """
                            SELECT title, place, disksdb.public.publisher.name, media_type, cost
                            FROM disksdb.public.disk
                            LEFT JOIN disksdb.public.publisher USING(publisher_id)
                            GROUP BY title, place, disksdb.public.publisher.name, media_type, cost
                            HAVING 
                            """;
            int filled = 0;

            if (!title.isEmpty()) {
                query += " LOWER(title) LIKE '%" + title.toLowerCase() + "%'\n";
                filled += 1;
            }
            if (!place.isEmpty()) {
                if (filled >= 1) query += " AND ";

                query += " LOWER(place) LIKE '%" + place.toLowerCase() + "%'\n";
                filled += 1;
            }
            if (!publisher.isEmpty()) {
                if (filled >= 1) query += " AND ";

                query += " LOWER(publisher.name) LIKE '%" + publisher.toLowerCase() + "%'\n";
                filled += 1;
            }
            if (!mediaType.isEmpty()) {
                if (filled >= 1) query += " AND ";

                query += " LOWER(media_type) LIKE '%" + mediaType.toLowerCase() + "%'\n";
                filled += 1;
            }
            if (costFrom != null && costTo != null) {
                if (filled >= 1) query += " AND ";

                query += " cost BETWEEN  " + costFrom + " AND " + costTo + "\n";
                filled += 1;
            } else {
                if (costFrom != null) {
                    if (filled >= 1) query += " AND ";

                    query += " cost >= " + costFrom + "\n";
                    filled += 1;
                } else {
                    if (costTo != null) {
                        if (filled >= 1) query += " AND ";

                        query += " cost <= " + costTo + "\n";
                        filled += 1;
                    }
                }
            }


            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            return toStringer(rs, "title", "place", "media_type", "name", "cost");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return null;
    }

    public String getDisksByCustomParams(String params) {

        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement("""
                    SELECT title, place, media_type, disksdb.public.publisher.name, disksdb.public.audio_disk.audios_on_disk, disksdb.public.document_disk.doc_on_disk, disksdb.public.software_disk.soft_on_disk, disk.comment
                    FROM disksdb.public.disk
                    LEFT JOIN disksdb.public.film_disk USING(disk_id)
                    LEFT JOIN disksdb.public.audio_disk USING(disk_id)
                    LEFT JOIN disksdb.public.document_disk USING(disk_id)
                    LEFT JOIN disksdb.public.software_disk USING(disk_id)
                    LEFT JOIN disksdb.public.publisher USING(publisher_id)
                    WHERE disk::text ~* all('{""" + params.toLowerCase() + """
                    }'::text[])
                    GROUP BY title, place, media_type, disksdb.public.publisher.name, disksdb.public.audio_disk.audios_on_disk, disksdb.public.document_disk.doc_on_disk, disksdb.public.software_disk.soft_on_disk, disk.comment;
                    """);
            
            ResultSet rs = pst.executeQuery();

            return toStringer(rs, "title", "place", "media_type", "name", "audios_on_disk", "doc_on_disk", "soft_on_disk");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return null;
    }

    public String getDisksByPublisher(String publisher) {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement(
                    """
                            SELECT title, place, publisher.name FROM disksdb.public.disk
                            JOIN disksdb.public.publisher USING(publisher_id)
                            WHERE LOWER(publisher.name) LIKE ?
                            """
            );

            pst.setString(1, "%" + publisher.toLowerCase() + "%");
            ResultSet rs = pst.executeQuery();

            return toStringer(rs, "title", "place", "name");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }
        return null;
    }

    public int getDiskCount() {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement(
                    """
                            SELECT COUNT(disk) as count
                            FROM disksdb.public.disk
                            """
            );

            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getInt("count");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return 0;
    }

    public double getAvgCost() {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement(
                    """
                            SELECT AVG(cost) as avgcost
                            FROM disksdb.public.disk
                            """
            );

            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getDouble("avgcost");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return 0;
    }

    public String getPopType() {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement(
                    """
                            SELECT media_type, COUNT(media_type)
                            FROM disksdb.public.disk
                            WHERE media_type IS NOT NULL
                            GROUP BY media_type
                            LIMIT 1
                            """
            );

            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getString("media_type");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return "None";
    }

    public String getPopPublisher() {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement(
                    """
                            SELECT disksdb.public.publisher.name, COUNT(disksdb.public.publisher.name) FROM disksdb.public.disk
                            JOIN disksdb.public.publisher USING(publisher_id)
                            GROUP BY disksdb.public.publisher.name
                            LIMIT 1
                            """
            );

            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getString("name");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return "None";
    }

    public int getFilmsNumber() {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement(
                    """
                            SELECT COUNT(title) as count
                            FROM disksdb.public.disk
                            JOIN disksdb.public.film_disk USING(disk_id)
                            LIMIT 1
                            """
            );

            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getInt("count");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return 0;
    }

    public int getAudioDiskNumber() {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement(
                    """
                            SELECT COUNT(title) as count
                            FROM disksdb.public.disk
                            JOIN disksdb.public.audio_disk USING(disk_id)
                            LIMIT 1
                            """
            );

            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getInt("count");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return 0;
    }

    public int getDocDiskNumber() {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement(
                    """
                            SELECT COUNT(title) as count 
                            FROM disksdb.public.disk
                            JOIN disksdb.public.document_disk USING(disk_id)
                            LIMIT 1
                            """
            );

            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getInt("count");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return 0;
    }

    public int getSoftDiskNumber() {
        try {
            Connection conn = dbConnection.connect();
            PreparedStatement pst = conn.prepareStatement(
                    """
                            SELECT COUNT(title) as count 
                            FROM disksdb.public.disk
                            JOIN disksdb.public.software_disk USING(disk_id)
                            LIMIT 1
                            """
            );

            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getInt("count");
        } catch (SQLException | ClassNotFoundException ex) {
            Alerter.alertError("Error while reading from DB \n" + ex.getMessage());
        }

        return 0;
    }

    private String toStringer(ResultSet rs, String... columns) throws SQLException {
        if (rs == null || columns == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        int length = columns.length;
        while (rs.next()) {
            for (int i = 0; i < length; i++) {
                String tempDBString = rs.getString(columns[i]);
                sb.append(Objects.requireNonNullElse(tempDBString, "NO DATA"));

                if (i + 1 < length) {
                    sb.append(" | ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
