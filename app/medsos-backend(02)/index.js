const sql = require('mssql');
const express = require('express');
const mysql = require('mysql');
const cors = require('cors');
const bodyParser = require('body-parser');
const fs = require('fs');
const path = require('path');

const multer = require('multer');
const admin = require('firebase-admin');
const serviceAccount = require('./ServiceKeyFCM.json');


const app = express();
const port = 5000;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use('/uploads', express.static('uploads'));
app.use(bodyParser.urlencoded({ extended: true }));
app.use('/uploads', express.static('uploads'));

const storage = multer.memoryStorage();
const upload = multer({ storage: storage });

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
});

// Connection pool configuration
const config = {
    user: process.env.user,
    password: process.env.password,
    server: process.env.server,
    database: process.env.database,
    options: {
        encrypt: true,
        trustServerCertificate: true,
    },
};

// Initialize connection pool
// Connection pool configuration (Ensure this is done only once)
const poolPromise = new sql.ConnectionPool(config)
    .connect()
    .then(pool => {
        console.log('Connected to SQL Server');
        return pool;
    })
    .catch(err => {
        console.error('SQL Server Connection Error:', err);
        process.exit(1); // Exit the process on connection failure
    });

// Helper function to get pool from the connection pool promise
const getPool = async () => {
    return poolPromise;
};

// Login peserta
app.post('/api/peserta/login', async (req, res) => {
    console.log('Login request received:', req.body);
    
    const { ktpa, password } = req.body;

    try {
        const pool = await getPool();

        const result = await pool.request()
            .input('ktpa', sql.VarChar, ktpa)
            .input('password', sql.VarChar, password)
            .query('SELECT * FROM peserta WHERE ktpa = @ktpa AND password = @password');

        if (result.recordset.length > 0) {
            const peserta = result.recordset[0];
            delete peserta.password;
            
            // Add session management logic here (e.g., create JWT token)
            res.json({
                success: true,
                message: 'Login successful',
                peserta: peserta
            });
        } else {
            res.json({
                success: false,
                message: 'KTPA atau Password anda salah'
            });
        }
    } catch (err) {
        console.error('Database error:', err);
        res.status(500).json({ success: false, message: err.message });
    }
});

// Logout logic (clear session or token)
app.post('/api/peserta/logout', (req, res) => {
    // Implement logout logic, clear session or JWT token
    res.json({
        success: true,
        message: 'Logout successful'
    });
});

// Endpoint untuk mengganti password
app.post('/api/peserta/reset-password', async (req, res) => {
    const { ktpa, newPassword } = req.body;

    try {
        const pool = await getPool();
        const result = await pool.request()
            .input('ktpa', sql.VarChar, ktpa)
            .input('newPassword', sql.VarChar, newPassword)
            .query('UPDATE peserta SET password = @newPassword WHERE ktpa = @ktpa');

        if (result.rowsAffected[0] > 0) {
            res.json({ success: true, message: 'Password berhasil diubah' });
        } else {
            res.json({ success: false, message: 'KTPA tidak ditemukan' });
        }
    } catch (err) {
        console.error('Database error:', err);
        res.status(500).json({ success: false, message: 'Internal Server Error' });
    }
});

app.post('/api/peserta/register', async (req, res) => {
    const { ktpa, nama, jenis_kelamin, password, email } = req.body;
    const created_at = new Date().toISOString().slice(0, 19).replace('T', ' ');

    const query = `
        INSERT INTO peserta (ktpa, nama, jenis_kelamin, password, created_at, updated_at, level, email)
        VALUES (@ktpa, @nama, @jenis_kelamin, @password, @created_at, @updated_at, @level, @email)
    `;

    try {
        const pool = await getPool();
        
        const result = await pool.request()
            .input('ktpa', sql.VarChar, ktpa)
            .input('nama', sql.VarChar, nama)
            .input('jenis_kelamin', sql.VarChar, jenis_kelamin)
            .input('password', sql.VarChar, password)
            .input('created_at', sql.DateTime, created_at)
            .input('updated_at', sql.DateTime, created_at)
            .input('level', sql.VarChar, 'peserta')
            .input('email', sql.VarChar, email)
            .query(query);

        res.status(201).json({ message: 'Pendaftaran berhasil' });
    } catch (err) {
        console.error('Database error:', err);
        res.status(500).json({ error: err.message });
    }
});

// Endpoint untuk memperbarui data peserta
app.put('/api/peserta/update', async (req, res) => {
    const { ktpa, nama, email, jenis_kelamin, bio } = req.body;

    // Validasi input
    if (!ktpa || !nama || !email) {
        return res.status(400).json({ error: 'KTPA, nama, dan email harus diisi' });
    }

    const query = `
        UPDATE peserta 
        SET nama = @nama, email = @email, jenis_kelamin = @jenis_kelamin, bio = @bio 
        WHERE ktpa = @ktpa
    `;

    try {
        const pool = await getPool();
        
        const result = await pool.request()
            .input('ktpa', sql.VarChar, ktpa)
            .input('nama', sql.VarChar, nama)
            .input('email', sql.VarChar, email)
            .input('jenis_kelamin', sql.VarChar, jenis_kelamin)
            .input('bio', sql.Text, bio)
            .query(query);

        if (result.rowsAffected[0] === 0) {
            return res.status(404).json({ error: 'Peserta tidak ditemukan' });
        }

        res.json({ message: 'Data peserta berhasil diperbarui' });
    } catch (err) {
        console.error('Database error:', err);
        res.status(500).json({ error: err.message });
    }
});

// Post event
app.post('/api/postev', upload.single('image'), async (req, res) => {
    console.log('Received data:', req.body);
    const image = req.file;
    const { ktpa, title, description, date, loc, kuota } = req.body;

    if (!title || !description) {
        console.error('Invalid data received');
        return res.status(400).send('Title and Description are required');
    }

    const formatDate = (date) => {
        const d = new Date(date);
        return d.toISOString(); // Format: 'yyyy-MM-ddTHH:mm:ss.SSSZ'
    };

    const formattedDate = formatDate(date);
    const created_at = new Date();

    const query = `INSERT INTO event (ktpa, evtitle, evdesc, evdate, evloc, evkuota, eventpic, created_at) 
                   VALUES (@ktpa, @title, @description, @date, @loc, @kuota, @image, @created_at)`;

    try {
        const pool = await getPool();
        const request = pool.request();
        request.input('ktpa', sql.VarChar, ktpa);
        request.input('title', sql.VarChar, title);
        request.input('description', sql.Text, description);
        request.input('date', sql.DateTime, formattedDate);
        request.input('loc', sql.VarChar, loc);
        request.input('kuota', sql.VarChar, kuota);
        request.input('image', sql.VarBinary, image.buffer);
        request.input('created_at', sql.DateTime, created_at);

        await request.query(query);

        const message = {
            notification: {
                title: 'Informasi Event Terbaru!',
                body: `${title}`,
            },
            data: {
                event_title: title,
                event_desc: description,
                event_date: formattedDate,
                event_location: loc,
                event_kuota: kuota,
            },
            topic: 'all',
        };

        admin.messaging().send(message)
            .then(response => {
                console.log('Successfully sent message:', response);
                res.status(200).send('Post created and notification sent');
            })
            .catch(error => {
                console.error('Error sending notification:', error);
                res.status(200).send('Post created but failed to send notification');
            });
    } catch (err) {
        console.error('Database error:', err);
        res.status(500).send('Database error');
    }
});

// Get events
app.get('/api/getev', async (req, res) => {
    const query = 'SELECT * FROM event ORDER BY created_at DESC';

    try {
        const pool = await getPool();
        const result = await pool.request().query(query);

        const posts = result.recordset.map(post => {
            if (post.eventpic) {
                post.media_url = `data:image/jpeg;base64,${Buffer.from(post.eventpic).toString('base64')}`;
            } else {
                post.media_url = null;
            }
            return post;
        });

        res.json(posts);
    } catch (err) {
        console.error('Database error:', err.message);
        res.status(500).json({ error: 'Database error' });
    }
});

app.get('/api/komunitas', async (req, res) => {
    try {
        // const pool = await sql.connect(config);
        const pool = await getPool();
        const result = await pool.request().query('SELECT * FROM komunitas');
        res.json(result.recordset);
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, message: 'Internal Server Error' });
    }
});

// Endpoint to join a community
app.post('/api/join-komunitas', async (req, res) => {
    const { user_ktpa, komunitas_id } = req.body;
    console.log('Received data:', req.body);

    try {
        // const pool = await sql.connect(config);

        const pool = await getPool();

        // Check if user is already in the community
        const checkQuery = 'SELECT * FROM anggota_komunitas WHERE komunitas_id = @komunitas_id AND user_ktpa = @user_ktpa';
        const checkResult = await pool.request()
            .input('komunitas_id', sql.Int, komunitas_id)
            .input('user_ktpa', sql.NVarChar(16), user_ktpa)
            .query(checkQuery);

        if (checkResult.recordset.length > 0) {
            return res.status(400).json({ success: false, message: 'Pengguna sudah bergabung dengan komunitas ini' });
        }

        // Insert the user into the community
        const insertQuery = 'INSERT INTO anggota_komunitas (komunitas_id, user_ktpa) VALUES (@komunitas_id, @user_ktpa)';
        await pool.request()
            .input('komunitas_id', sql.Int, komunitas_id)
            .input('user_ktpa', sql.NVarChar(16), user_ktpa)
            .query(insertQuery);

        res.status(200).json({ success: true, message: 'Berhasil bergabung dengan komunitas' });
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, message: 'Internal Server Error' });
    }
});

// Endpoint to leave a community
app.post('/api/unjoin-komunitas', async (req, res) => {
    const { user_ktpa, komunitas_id } = req.body;
    console.log('Received data:', req.body);

    try {
        // const pool = await sql.connect(config);
        const pool = await getPool();

        // Check if user is already in the community
        const checkQuery = 'SELECT * FROM anggota_komunitas WHERE komunitas_id = @komunitas_id AND user_ktpa = @user_ktpa';
        const checkResult = await pool.request()
            .input('komunitas_id', sql.Int, komunitas_id)
            .input('user_ktpa', sql.NVarChar(16), user_ktpa)
            .query(checkQuery);

        if (checkResult.recordset.length === 0) {
            return res.status(400).json({ success: false, message: 'Pengguna belum bergabung dengan komunitas ini' });
        }

        // Delete user from the community
        const deleteQuery = 'DELETE FROM anggota_komunitas WHERE komunitas_id = @komunitas_id AND user_ktpa = @user_ktpa';
        await pool.request()
            .input('komunitas_id', sql.Int, komunitas_id)
            .input('user_ktpa', sql.NVarChar(16), user_ktpa)
            .query(deleteQuery);

        res.status(200).json({ success: true, message: 'Berhasil meninggalkan komunitas' });
    } catch (err) {
        console.error(err);
        res.status(500).json({ success: false, message: 'Internal Server Error' });
    }
});

// Endpoint to check if user has joined a community
app.post('/api/is-user-joined', async (req, res) => {
    const { user_ktpa, komunitas_id } = req.body;

    console.log('Checking join status for:', user_ktpa, 'in komunitas', komunitas_id);

    try {
        // const pool = await sql.connect(config);
        const pool = await getPool();

        // Check if user is in the community
        const query = 'SELECT * FROM anggota_komunitas WHERE komunitas_id = @komunitas_id AND user_ktpa = @user_ktpa';
        const result = await pool.request()
            .input('komunitas_id', sql.Int, komunitas_id)
            .input('user_ktpa', sql.NVarChar(16), user_ktpa)
            .query(query);

        if (result.recordset.length > 0) {
            return res.status(200).json(true); // User is joined
        } else {
            return res.status(200).json(false); // User is not joined
        }
    } catch (err) {
        console.error('Error executing query:', err);
        return res.status(500).json({ success: false, message: 'Internal Server Error' });
    }
});

// Endpoint for a user to leave a community
app.post('/api/leave-komunitas', async (req, res) => {
    const { user_ktpa, komunitas_id } = req.body;

    console.log('Leaving community for:', user_ktpa, 'in komunitas', komunitas_id);

    try {
        // const pool = await sql.connect(config);
        const pool = await getPool();


        // Check if user is already in the community
        const checkQuery = 'SELECT * FROM anggota_komunitas WHERE komunitas_id = @komunitas_id AND user_ktpa = @user_ktpa';
        const checkResult = await pool.request()
            .input('komunitas_id', sql.Int, komunitas_id)
            .input('user_ktpa', sql.NVarChar(16), user_ktpa)
            .query(checkQuery);

        if (checkResult.recordset.length === 0) {
            return res.status(400).json({ success: false, message: 'Pengguna belum bergabung dengan komunitas ini' });
        }

        // Delete user from the community
        const deleteQuery = 'DELETE FROM anggota_komunitas WHERE komunitas_id = @komunitas_id AND user_ktpa = @user_ktpa';
        await pool.request()
            .input('komunitas_id', sql.Int, komunitas_id)
            .input('user_ktpa', sql.NVarChar(16), user_ktpa)
            .query(deleteQuery);

        return res.status(200).json({ success: true, message: 'Berhasil meninggalkan komunitas' });
    } catch (err) {
        console.error('Error executing delete query:', err);
        return res.status(500).json({ success: false, message: 'Internal Server Error' });
    }
});

app.get('/api/posts', async (req, res) => {
    const query = `
        SELECT p.*, pe.nama,
        (SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count
        FROM post p 
        JOIN peserta pe ON p.ktpa = pe.ktpa 
        ORDER BY p.created_at DESC
    `;

    try {
        // Connect to the database
        let pool = await sql.connect(config);

        // Perform the query
        const result = await pool.request().query(query);

        const posts = result.recordset.map(post => {
            if (post.media_url) {
                post.media_url = Buffer.from(post.media_url).toString('base64');
            }
            return {
                ...post,
                comment_count: parseInt(post.comment_count) || 0
            };
        });

        res.json(posts);

    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        // Close the database connection
        sql.close();
    }
});

app.get('/api/posts/:id', async (req, res) => {
    const query = `
        SELECT p.*, pe.nama,
        (SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count
        FROM post p 
        JOIN peserta pe ON p.ktpa = pe.ktpa 
        WHERE p.id = @id
    `;

    try {
        // Connect to the database
        let pool = await sql.connect(config);

        // Perform the query
        const result = await pool.request()
            .input('id', sql.Int, req.params.id)
            .query(query);

        if (result.recordset.length > 0) {
            const post = result.recordset[0];
            if (post.media_url) {
                post.media_url = Buffer.from(post.media_url).toString('base64');
            }
            post.comment_count = parseInt(post.comment_count) || 0;
            res.json(post);
        } else {
            res.status(404).json({ message: 'Post not found' });
        }

    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        // Close the database connection
        sql.close();
    }
});

// Get comments for a specific post
app.get('/api/posts/:postId/comments', async (req, res) => {
    const query = `
        SELECT c.*, p.nama 
        FROM comment c
        JOIN peserta p ON c.ktpa = p.ktpa
        WHERE c.post_id = @postId
        ORDER BY c.created_at DESC
    `;

    try {
        // Connect to the database
        let pool = await sql.connect(config);

        // Perform the query
        const result = await pool.request()
            .input('postId', sql.Int, req.params.postId)
            .query(query);

        res.json(result.recordset);

    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        // Close the database connection
        sql.close();
    }
});

// Create a new comment
app.post('/api/comments', async (req, res) => {
    const { post_id, ktpa, content } = req.body;
    const created_at = new Date().toISOString().slice(0, 19).replace('T', ' ');
    const updated_at = created_at;

    const query = `
        INSERT INTO comment (post_id, ktpa, content, created_at, updated_at)
        VALUES (@post_id, @ktpa, @content, @created_at, @updated_at);
        
        SELECT SCOPE_IDENTITY() AS id;
    `;

    console.log("Received request:", req.body);  // Log input data
    
    try {
        // Connect to the database
        let pool = await sql.connect(config);

        // Perform the query
        const result = await pool.request()
            .input('post_id', sql.Int, post_id)
            .input('ktpa', sql.VarChar, ktpa)
            .input('content', sql.Text, content)
            .input('created_at', sql.DateTime, created_at)
            .input('updated_at', sql.DateTime, updated_at)
            .query(query);

        console.log("Insert result:", result);  // Log insert result

        // Ensure we have a valid ID from the insert
        const newCommentId = result.recordset && result.recordset.length > 0 ? result.recordset[0].id : null;
        if (!newCommentId) {
            throw new Error('Failed to retrieve new comment ID');
        }

        // Get updated comment count
        const countQuery = 'SELECT COUNT(*) as count FROM comment WHERE post_id = @post_id';
        const countResult = await pool.request()
            .input('post_id', sql.Int, post_id)
            .query(countQuery);

        console.log("Comment count:", countResult.recordset[0].count);  // Log comment count
        
        const comment_count = countResult.recordset[0].count;

        // Return the new comment with count
        const selectQuery = `
            SELECT c.*, p.nama 
            FROM comment c
            JOIN peserta p ON c.ktpa = p.ktpa
            WHERE c.id = @id
        `;

        console.log("Fetching comment details for ID:", newCommentId);

        const commentResult = await pool.request()
            .input('id', sql.Int, newCommentId)
            .query(selectQuery);

        console.log("Comment details:", commentResult.recordset[0]);  // Log comment details

        res.json({
            message: 'Comment created successfully',
            comment: commentResult.recordset[0],
            comment_count: comment_count
        });

    } catch (err) {
        console.error("Error:", err.message);  // Log error if any
        res.status(500).json({ error: err.message });
    } finally {
        // Close the database connection
        sql.close();
    }
});

// Delete a comment
app.delete('/api/comments/:id', async (req, res) => {
    const query = 'DELETE FROM comment WHERE id = @id';

    try {
        // Connect to the database
        let pool = await sql.connect(config);

        // Perform the query
        await pool.request()
            .input('id', sql.Int, req.params.id)
            .query(query);

        res.json({ message: 'Comment deleted successfully' });

    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        // Close the database connection
        sql.close();
    }
});

// Toggle like (like/unlike)
app.post('/api/posts/:postId/like', async (req, res) => {
    const { ktpa } = req.body;
    const postId = req.params.postId;
    const created_at = new Date().toISOString().slice(0, 19).replace('T', ' ');

    try {
        // Connect to the database
        let pool = await sql.connect(config);

        // Check if like exists
        const checkQuery = 'SELECT id FROM likes WHERE post_id = @postId AND ktpa = @ktpa';
        const checkResult = await pool.request()
            .input('postId', sql.Int, postId)
            .input('ktpa', sql.VarChar, ktpa)
            .query(checkQuery);

        if (checkResult.recordset.length > 0) {
            // Unlike: Remove existing like
            const deleteQuery = 'DELETE FROM likes WHERE post_id = @postId AND ktpa = @ktpa';
            await pool.request()
                .input('postId', sql.Int, postId)
                .input('ktpa', sql.VarChar, ktpa)
                .query(deleteQuery);

            res.json({ liked: false, message: 'Post unliked successfully' });
        } else {
            // Like: Add new like
            const insertQuery = 'INSERT INTO likes (post_id, ktpa, created_at) VALUES (@postId, @ktpa, @created_at)';
            await pool.request()
                .input('postId', sql.Int, postId)
                .input('ktpa', sql.VarChar, ktpa)
                .input('created_at', sql.DateTime, created_at)
                .query(insertQuery);

            res.json({ liked: true, message: 'Post liked successfully' });
        }

    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        // Close the database connection
        sql.close();
    }
});
// Get like count for a specific post
app.get('/api/posts/:postId/likes', async (req, res) => {
    const postId = req.params.postId;

    const query = 'SELECT COUNT(*) as likeCount FROM likes WHERE post_id = @postId';

    try {
        let pool = await sql.connect(config);
        const result = await pool.request()
            .input('postId', sql.Int, postId)
            .query(query);

        res.json({
            likeCount: result.recordset[0].likeCount
        });
    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        sql.close();
    }
});

// Update post
app.put('/api/posts/:id', async (req, res) => {
    const postId = req.params.id;
    const { content } = req.body;
    const updated_at = new Date().toISOString().slice(0, 19).replace('T', ' ');

    const query = 'UPDATE post SET content = @content, updated_at = @updated_at WHERE id = @postId';

    try {
        let pool = await sql.connect(config);
        const result = await pool.request()
            .input('content', sql.Text, content)
            .input('updated_at', sql.DateTime, updated_at)
            .input('postId', sql.Int, postId)
            .query(query);

        if (result.rowsAffected[0] === 0) {
            res.status(404).json({ message: 'Post not found' });
            return;
        }

        res.json({
            message: 'Post updated successfully',
            post: {
                id: postId,
                content: content,
                updated_at: updated_at
            }
        });
    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        sql.close();
    }
});

// Delete all likes for a specific post
app.delete('/api/posts/:postId/likes', async (req, res) => {
    const postId = req.params.postId;

    const query = 'DELETE FROM likes WHERE post_id = @postId';

    try {
        let pool = await sql.connect(config);
        await pool.request()
            .input('postId', sql.Int, postId)
            .query(query);

        res.json({ message: 'All likes deleted successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        sql.close();
    }
});

// Delete all comments for a specific post
app.delete('/api/posts/:postId/comments', async (req, res) => {
    const postId = req.params.postId;

    const query = 'DELETE FROM comment WHERE post_id = @postId';

    try {
        let pool = await sql.connect(config);
        await pool.request()
            .input('postId', sql.Int, postId)
            .query(query);

        res.json({ message: 'All comments deleted successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        sql.close();
    }
});

// Update delete post endpoint to handle cascading deletes
app.delete('/api/posts/:id', async (req, res) => {
    const postId = req.params.id;

    try {
        let pool = await sql.connect(config);

        // Start transaction
        const transaction = new sql.Transaction(pool);

        await transaction.begin();

        try {
            // First delete likes
            const deleteLikesQuery = 'DELETE FROM likes WHERE post_id = @postId';
            await transaction.request()
                .input('postId', sql.Int, postId)
                .query(deleteLikesQuery);

            // Then delete comments
            const deleteCommentsQuery = 'DELETE FROM comment WHERE post_id = @postId';
            await transaction.request()
                .input('postId', sql.Int, postId)
                .query(deleteCommentsQuery);

            // Finally delete the post
            const deletePostQuery = 'DELETE FROM post WHERE id = @postId';
            const result = await transaction.request()
                .input('postId', sql.Int, postId)
                .query(deletePostQuery);

            if (result.rowsAffected[0] === 0) {
                await transaction.rollback();
                res.status(404).json({ message: 'Post not found' });
                return;
            }

            await transaction.commit();

            res.json({ message: 'Post and all related data deleted successfully' });

        } catch (err) {
            await transaction.rollback();
            res.status(500).json({ error: err.message });
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        sql.close();
    }
});

const videoChunks = {};

app.post('/api/post', async (req, res) => {
    const { ktpa, content, created_at, updated_at, media_url, media_type } = req.body;

    try {
        // Get the connection pool
        const pool = await getPool();
        console.log('Request body:', req.body);

        // Check if this is a chunked upload
        if (media_type && media_type.includes('video_chunk')) {
            // Extract chunk index and total chunks from media_type
            const [_, chunkIndex, totalChunks] = media_type.match(/video_chunk_(\d+)_of_(\d+)/);

            // Initialize chunk storage if doesn't exist
            if (!videoChunks[ktpa]) {
                videoChunks[ktpa] = {
                    chunks: [],
                    content,
                    created_at,
                    updated_at
                };
            }

            // Add this chunk to the array
            videoChunks[ktpa].chunks[parseInt(chunkIndex)] = media_url;

            // Check if all chunks are received
            if (videoChunks[ktpa].chunks.filter(Boolean).length === parseInt(totalChunks)) {
                // Combine all chunks into one complete video (in base64)
                const completeVideo = videoChunks[ktpa].chunks.join('');

                // Create the post with complete video data
                const query = `
                    INSERT INTO post (ktpa, content, created_at, updated_at, media_url, media_type) 
                    OUTPUT INSERTED.id
                    VALUES (@ktpa, @content, @created_at, @updated_at, @media_url, @media_type)
                `;
                
                const mediaBlob = Buffer.from(completeVideo, 'base64'); // Convert base64 string to binary blob

                // Insert post into the database
                const result = await pool.request()
                    .input('ktpa', sql.VarChar, ktpa)
                    .input('content', sql.Text, videoChunks[ktpa].content)
                    .input('created_at', sql.DateTime, videoChunks[ktpa].created_at)
                    .input('updated_at', sql.DateTime, videoChunks[ktpa].updated_at)
                    .input('media_url', sql.VarBinary, mediaBlob)
                    .input('media_type', sql.VarChar, 'video')
                    .query(query);

                if (!result.recordset || result.recordset.length === 0) {
                    throw new Error('Failed to get the inserted post ID.');
                }

                // Clear the video chunks after saving
                delete videoChunks[ktpa];

                // Fetch the inserted post with user info
                const selectQuery = `
                    SELECT p.*, pe.nama 
                    FROM post p 
                    JOIN peserta pe ON p.ktpa = pe.ktpa 
                    WHERE p.id = @id
                `;

                const postResult = await pool.request()
                    .input('id', sql.Int, result.recordset[0].id)
                    .query(selectQuery);

                res.json({
                    message: 'Post created successfully',
                    post: postResult.recordset[0]
                });
            } else {
                // Not all chunks are received yet
                res.json({ message: `Chunk ${parseInt(chunkIndex) + 1} of ${totalChunks} received` });
            }
        } else if (!media_type && !media_url){
            // Handle data tanpa media
            const query = `
                INSERT INTO post (ktpa, content, created_at, updated_at, media_url, media_type) 
                OUTPUT INSERTED.id
                VALUES (@ktpa, @content, @created_at, @updated_at, NULL, NULL)
            `;

            const result = await pool.request()
                .input('ktpa', sql.VarChar, ktpa)
                .input('content', sql.Text, content)
                .input('created_at', sql.DateTime, created_at)
                .input('updated_at', sql.DateTime, updated_at)
                .query(query);

            if (!result.recordset || result.recordset.length === 0) {
                throw new Error('Failed to insert post without media.');
            }

            const selectQuery = `
                SELECT p.*, pe.nama 
                FROM post p 
                JOIN peserta pe ON p.ktpa = pe.ktpa 
                WHERE p.id = @id
            `;

            const postResult = await pool.request()
                .input('id', sql.Int, result.recordset[0].id)
                .query(selectQuery);

            res.json({
                message: 'Post without media created successfully',
                post: postResult.recordset[0],
            });
        } else {
            // Handle non-chunked uploads as before
            const query = `
                INSERT INTO post (ktpa, content, created_at, updated_at, media_url, media_type) 
                OUTPUT INSERTED.id
                VALUES (@ktpa, @content, @created_at, @updated_at, @media_url, @media_type)
            `;
            
            const mediaBlob = media_url ? Buffer.from(media_url, 'base64') : null; // Convert base64 string to binary

            // Insert post into the database
            const result = await pool.request()
                .input('ktpa', sql.VarChar, ktpa)
                .input('content', sql.Text, content)
                .input('created_at', sql.DateTime, created_at)
                .input('updated_at', sql.DateTime, updated_at)
                .input('media_url', mediaBlob ? sql.VarBinary : sql.Null, mediaBlob)
                .input('media_type', sql.VarChar, media_type)
                .query(query);

            if (!result.recordset || result.recordset.length === 0) {
                throw new Error('Failed to get the inserted post ID.');
            }

            // Fetch the inserted post with user info
            const selectQuery = `
                SELECT p.*, pe.nama 
                FROM post p 
                JOIN peserta pe ON p.ktpa = pe.ktpa 
                WHERE p.id = @id
            `;
            
            const postResult = await pool.request()
                .input('id', sql.Int, result.recordset[0].id)
                .query(selectQuery);

            res.json({
                message: 'Post created successfully',
                post: postResult.recordset[0]
            });
        }
    } catch (err) {
        console.error('Error saving post:', err);
        res.status(500).json({ error: err.message });
    }
});

// Get all news
app.get('/api/news', async (req, res) => {
    try {
        await sql.connect(dbConfig);
        const result = await sql.query`SELECT id, title, description, date, created_at, updated_at, image, author FROM news ORDER BY created_at DESC`;
        res.json(result.recordset);
    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        sql.close();
    }
});

// Get single news by id
app.get('/api/news/:id', async (req, res) => {
    const { id } = req.params;
    try {
        await sql.connect(dbConfig);
        const result = await sql.query`SELECT id, title, description, date, created_at, updated_at, image, author FROM news WHERE id = ${id}`;
        if (result.recordset.length > 0) {
            res.json(result.recordset[0]);
        } else {
            res.status(404).json({ message: 'News not found' });
        }
    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        sql.close();
    }
});

// Mendapatkan semua berita
app.get('/news', async (req, res) => {
    try {
        await sql.connect(dbConfig);
        const result = await sql.query`SELECT * FROM news`;
        res.json(result.recordset);
    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        sql.close();
    }
});

// Menambahkan berita baru dengan gambar
app.post('/news', async (req, res) => {
    const { date, title, description, image, author } = req.body;
    try {
        await sql.connect(dbConfig);
        const result = await sql.query`INSERT INTO news (date, title, description, image, author) VALUES (${date}, ${title}, ${description}, ${image}, ${author})`;
        res.status(201).json({ id: result.recordset.insertId, date, title, description, image, author });
    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        sql.close();
    }
});

// Memperbarui berita
app.put('/news/:id', async (req, res) => {
    const { id } = req.params;
    const { date, title, description } = req.body;
    try {
        await sql.connect(dbConfig);
        await sql.query`UPDATE news SET date = ${date}, title = ${title}, description = ${description} WHERE id = ${id}`;
        res.json({ id, date, title, description });
    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        sql.close();
    }
});

// Menghapus berita
app.delete('/news/:id', async (req, res) => {
    const { id } = req.params;
    try {
        await sql.connect(dbConfig);
        await sql.query`DELETE FROM news WHERE id = ${id}`;
        res.status(204).send();
    } catch (err) {
        res.status(500).json({ error: err.message });
    } finally {
        sql.close();
    }
});

// Endpoint untuk mengambil semua post dari komunitas
app.get('/api/getpkomun', async (req, res) => {
    const komunitasId = req.query.komunitasId;

    if (!komunitasId) {
        return res.status(400).json({ error: 'komunitasId is required' });
    }

    const query = `
        SELECT p.*, pe.nama,
        (SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count
        FROM post_komun p
        JOIN peserta pe ON p.user_ktpa = pe.ktpa
        WHERE p.komunitas_id = @komunitasId
        ORDER BY p.created_at DESC
    `;

    try {
        const pool = await getPool(); // Gunakan pool
        const result = await pool.request()
            .input('komunitasId', sql.VarChar, komunitasId)
            .query(query);

        const posts = result.recordset.map(post => {
            if (post.media_url) {
                post.media_url = post.media_url.toString('base64');
            }
            return {
                ...post,
                comment_count: parseInt(post.comment_count) || 0
            };
        });

        res.json(posts);
    } catch (err) {
        console.error('Error fetching posts:', err);
        res.status(500).json({ error: err.message });
    }
});

// Endpoint untuk mengambil post tertentu dari komunitas
app.get('/api/getpkomun/:id', async (req, res) => {
    const komunitasId = req.query.komunitasId;
    const postId = req.params.id;

    if (!komunitasId) {
        return res.status(400).json({ error: 'komunitasId is required' });
    }

    const query = `
        SELECT p.*, pe.nama,
        (SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count
        FROM post_komun p
        JOIN peserta pe ON p.user_ktpa = pe.ktpa
        WHERE p.id = @postId AND p.komunitas_id = @komunitasId
    `;

    try {
        const pool = await getPool(); // Gunakan pool
        const result = await pool.request()
            .input('postId', sql.Int, postId)
            .input('komunitasId', sql.VarChar, komunitasId)
            .query(query);

        if (result.recordset.length > 0) {
            const post = result.recordset[0];
            if (post.media_url) {
                post.media_url = post.media_url.toString('base64');
            }
            post.comment_count = parseInt(post.comment_count) || 0;
            res.json(post);
        } else {
            res.status(404).json({ message: 'Post not found' });
        }
    } catch (err) {
        console.error('Error fetching post:', err);
        res.status(500).json({ error: err.message });
    }
});

// Endpoint untuk mengambil komentar dari post tertentu
app.get('/api/getpkomun/:postId/comments', async (req, res) => {
    const komunitasId = req.query.komunitasId;
    const postId = req.params.postId;

    if (!komunitasId) {
        return res.status(400).json({ error: 'komunitasId is required' });
    }

    const query = `
        SELECT c.*, p.nama
        FROM comment_komun c
        JOIN peserta p ON c.ktpa = p.ktpa
        WHERE c.post_id = @postId
        ORDER BY c.created_at DESC
    `;

    try {
        const pool = await getPool(); // Gunakan pool
        const result = await pool.request()
            .input('postId', sql.Int, postId)
            .query(query);

        res.json(result.recordset);
    } catch (err) {
        console.error('Error fetching comments:', err);
        res.status(500).json({ error: err.message });
    }
});

app.post('/api/commentskomun', async (req, res) => {
    const { post_id, ktpa, content } = req.body;
    const komunitas_id = req.query.komunitasId;
    const created_at = new Date().toISOString().slice(0, 19).replace('T', ' ');
    const updated_at = created_at;

    if (!komunitas_id || !ktpa || !post_id || !content) {
        return res.status(400).json({ error: 'All fields are required' });
    }

    const checkMembershipQuery = `
        SELECT COUNT(*) AS count 
        FROM anggota_komunitas 
        WHERE komunitas_id = @komunitasId AND user_ktpa = @ktpa
    `;

    const insertCommentQuery = `
        INSERT INTO comment_komun (post_id, komunitas_id, ktpa, content, created_at, updated_at)
        OUTPUT INSERTED.id
        VALUES (@postId, @komunitasId, @ktpa, @content, @created_at, @updated_at)
    `;

    const countCommentsQuery = `
        SELECT COUNT(*) AS count 
        FROM comment 
        WHERE post_id = @postId
    `;

    const getNewCommentQuery = `
        SELECT c.*, p.nama 
        FROM comment_komun c
        JOIN peserta p ON c.ktpa = p.ktpa
        WHERE c.id = @commentId
    `;

    try {
        const pool = await getPool();

        // Check if user is a member of the community
        const membershipResult = await pool.request()
            .input('komunitasId', sql.VarChar, komunitas_id)
            .input('ktpa', sql.VarChar, ktpa)
            .query(checkMembershipQuery);

        if (membershipResult.recordset[0].count === 0) {
            return res.status(403).json({ error: 'Anda harus bergabung ke komunitas untuk membuat komentar.' });
        }

        // Insert comment
        const insertResult = await pool.request()
            .input('postId', sql.Int, post_id)
            .input('komunitasId', sql.VarChar, komunitas_id)
            .input('ktpa', sql.VarChar, ktpa)
            .input('content', sql.Text, content)
            .input('created_at', sql.DateTime, created_at)
            .input('updated_at', sql.DateTime, updated_at)
            .query(insertCommentQuery);

        const commentId = insertResult.recordset[0].id;

        // Count comments
        const countResult = await pool.request()
            .input('postId', sql.Int, post_id)
            .query(countCommentsQuery);

        const commentCount = countResult.recordset[0].count;

        // Get new comment details
        const commentResult = await pool.request()
            .input('commentId', sql.Int, commentId)
            .query(getNewCommentQuery);

        res.json({
            message: 'Comment created successfully',
            comment: commentResult.recordset[0],
            comment_count: commentCount,
        });
    } catch (err) {
        console.error('Error creating comment:', err);
        res.status(500).json({ error: err.message });
    }
});

// Endpoint untuk memberikan/menarik like pada post
app.post('/api/getpkomun/:postId/like', async (req, res) => {
    const { ktpa, komunitasId } = req.body;
    const postId = req.params.postId;
    const created_at = new Date().toISOString().slice(0, 19).replace('T', ' ');

    if (!komunitasId || !ktpa) {
        return res.status(400).json({ error: 'komunitasId and ktpa are required' });
    }

    const checkLikeQuery = `
        SELECT id 
        FROM likes_komun 
        WHERE post_id = @postId AND ktpa = @ktpa AND komunitas_id = @komunitasId
    `;

    const insertLikeQuery = `
        INSERT INTO likes_komun (post_id, komunitas_id, ktpa, created_at)
        VALUES (@postId, @komunitasId, @ktpa, @created_at)
    `;

    const deleteLikeQuery = `
        DELETE FROM likes_komun 
        WHERE post_id = @postId AND ktpa = @ktpa AND komunitas_id = @komunitasId
    `;

    try {
        const pool = await getPool();

        // Check if the like already exists
        const likeResult = await pool.request()
            .input('postId', sql.Int, postId)
            .input('ktpa', sql.VarChar, ktpa)
            .input('komunitasId', sql.Int, parseInt(komunitasId))
            .query(checkLikeQuery);

        if (likeResult.recordset.length > 0) {
            // Unlike
            await pool.request()
                .input('postId', sql.Int, postId)
                .input('ktpa', sql.VarChar, ktpa)
                .input('komunitasId', sql.Int, parseInt(komunitasId))
                .query(deleteLikeQuery);

            res.json({ liked: false, message: 'Post unliked successfully' });
        } else {
            // Like
            await pool.request()
                .input('postId', sql.Int, postId)
                .input('komunitasId', sql.Int, parseInt(komunitasId))
                .input('ktpa', sql.VarChar, ktpa)
                .input('created_at', sql.DateTime, created_at)
                .query(insertLikeQuery);

            res.json({ liked: true, message: 'Post liked successfully' });
        }
    } catch (err) {
        console.error('Error toggling like:', err);
        res.status(500).json({ error: err.message });
    }
});

// Endpoint untuk mengambil jumlah like dari post tertentu
app.get('/api/getpkomun/:postId/likes', async (req, res) => {
    const postId = req.params.postId;

    const query = `
        SELECT COUNT(*) AS likeCount 
        FROM likes_komun 
        WHERE post_id = @postId
    `;

    try {
        const pool = await getPool();

        const result = await pool.request()
            .input('postId', sql.Int, postId)
            .query(query);

        res.json({ likeCount: result.recordset[0].likeCount });
    } catch (err) {
        console.error('Error fetching like count:', err);
        res.status(500).json({ error: err.message });
    }
});

app.put('/api/getpkomun/:id', async (req, res) => {
    const postId = req.params.id;
    const { content } = req.body;
    const updated_at = new Date().toISOString().slice(0, 19).replace('T', ' ');

    const query = `
        UPDATE post_komun 
        SET content = @content, updated_at = @updated_at 
        WHERE id = @postId
    `;

    try {
        const pool = await getPool();
        const result = await pool.request()
            .input('content', sql.Text, content)
            .input('updated_at', sql.DateTime, updated_at)
            .input('postId', sql.Int, postId)
            .query(query);

        if (result.rowsAffected[0] === 0) {
            return res.status(404).json({ message: 'Post not found' });
        }

        res.json({
            message: 'Post updated successfully',
            post: {
                id: postId,
                content: content,
                updated_at: updated_at,
            },
        });
    } catch (err) {
        console.error('Error updating post:', err);
        res.status(500).json({ error: err.message });
    }
});

// Hapus post dan data terkait
app.delete('/api/getpkomun/:id', async (req, res) => {
    const postId = req.params.id;

    try {
        const pool = await getPool();

        // Hapus likes
        await pool.request()
            .input('postId', sql.Int, postId)
            .query('DELETE FROM likes_komun WHERE post_id = @postId');

        // Hapus komentar
        await pool.request()
            .input('postId', sql.Int, postId)
            .query('DELETE FROM comment_komun WHERE post_id = @postId');

        // Hapus post
        const deletePostResult = await pool.request()
            .input('postId', sql.Int, postId)
            .query('DELETE FROM post_komun WHERE id = @postId');

        if (deletePostResult.rowsAffected[0] === 0) {
            return res.status(404).json({ message: 'Post not found' });
        }

        res.json({ message: 'Post and all related data deleted successfully' });
    } catch (err) {
        console.error('Error deleting post:', err);
        res.status(500).json({ error: err.message });
    }
});

// Hapus semua likes pada post tertentu
app.delete('/api/getpkomun/:postId/likes', async (req, res) => {
    const postId = req.params.postId;

    try {
        const pool = await getPool();
        await pool.request()
            .input('postId', sql.Int, postId)
            .query('DELETE FROM likes_komun WHERE post_id = @postId');

        res.json({ message: 'All likes deleted successfully' });
    } catch (err) {
        console.error('Error deleting likes:', err);
        res.status(500).json({ error: err.message });
    }
});

// Hapus semua komentar pada post tertentu
app.delete('/api/getpkomun/:postId/comments', async (req, res) => {
    const postId = req.params.postId;

    try {
        const pool = await getPool();
        await pool.request()
            .input('postId', sql.Int, postId)
            .query('DELETE FROM comment_komun WHERE post_id = @postId');

        res.json({ message: 'All comments deleted successfully' });
    } catch (err) {
        console.error('Error deleting comments:', err);
        res.status(500).json({ error: err.message });
    }
});

// Ambil jumlah likes pada post tertentu
app.get('/api/getpkomun/:postId/likes', async (req, res) => {
    const postId = req.params.postId;

    const query = `
        SELECT COUNT(*) AS likeCount 
        FROM likes_komun 
        WHERE post_id = @postId
    `;

    try {
        const pool = await getPool();
        const result = await pool.request()
            .input('postId', sql.Int, postId)
            .query(query);

        res.json({ likeCount: result.recordset[0].likeCount });
    } catch (err) {
        console.error('Error fetching like count:', err);
        res.status(500).json({ error: err.message });
    }
});

app.post('/api/buat-post', async (req, res) => {
    const { user_ktpa, konten, created_at, updated_at, media_url, media_type, komunitas_id } = req.body;

    try {
        // Get the connection pool
        const pool = await getPool();
        console.log('Request body:', req.body);

        // Cek apakah pengguna sudah bergabung dalam komunitas
        const checkMemberQuery = `
            SELECT * FROM anggota_komunitas 
            WHERE komunitas_id = @komunitas_id AND user_ktpa = @user_ktpa
        `;
        console.log(`Executing query: ${checkMemberQuery}`);
        console.log(`With parameters: komunitas_id=${komunitas_id}, user_ktpa=${user_ktpa}`);

        const memberCheckResult = await pool.request()
            .input('komunitas_id', sql.Int, komunitas_id)
            .input('user_ktpa', sql.VarChar, user_ktpa)
            .query(checkMemberQuery);

        console.log('Member check result:', memberCheckResult);

        if (memberCheckResult.recordset.length === 0) {
            return res.status(400).json({ message: 'Pengguna harus bergabung terlebih dahulu untuk membuat postingan' });
        }

        // Proses unggahan media
        if (media_type && media_type.includes('video_chunk')) {
            const [_, chunkIndex, totalChunks] = media_type.match(/video_chunk_(\d+)_of_(\d+)/);

            if (!videoChunks[user_ktpa]) {
                videoChunks[user_ktpa] = {
                    chunks: [],
                    content: konten,
                    created_at,
                    updated_at
                };
            }

            videoChunks[user_ktpa].chunks[parseInt(chunkIndex)] = media_url;

            if (videoChunks[user_ktpa].chunks.filter(Boolean).length === parseInt(totalChunks)) {
                const completeVideo = videoChunks[user_ktpa].chunks.join('');

                const query = `
                    INSERT INTO post_komun (user_ktpa, content, created_at, updated_at, media_url, media_type, komunitas_id) 
                    OUTPUT INSERTED.id
                    VALUES (@user_ktpa, @content, @created_at, @updated_at, @media_url, @media_type, @komunitas_id)
                `;

                const mediaBlob = Buffer.from(completeVideo, 'base64');

                const result = await pool.request()
                    .input('user_ktpa', sql.VarChar, user_ktpa)
                    .input('content', sql.Text, videoChunks[user_ktpa].content)
                    .input('created_at', sql.DateTime, videoChunks[user_ktpa].created_at)
                    .input('updated_at', sql.DateTime, videoChunks[user_ktpa].updated_at)
                    .input('media_url', sql.VarBinary, mediaBlob)
                    .input('media_type', sql.VarChar, 'video')
                    .input('komunitas_id', sql.Int, komunitas_id)
                    .query(query);

                if (!result.recordset || result.recordset.length === 0) {
                    throw new Error('Failed to get the inserted post ID.');
                }

                delete videoChunks[user_ktpa];

                const selectQuery = `
                    SELECT p.*, pe.nama 
                    FROM post_komun p 
                    JOIN peserta pe ON p.user_ktpa = pe.ktpa 
                    WHERE p.id = @id
                `;

                const postResult = await pool.request()
                    .input('id', sql.Int, result.recordset[0].id)
                    .query(selectQuery);

                res.json({
                    message: 'Post created successfully',
                    post: postResult.recordset[0]
                });
            } else {
                res.json({ message: `Chunk ${parseInt(chunkIndex) + 1} of ${totalChunks} received` });
            }
        } else if (!media_type && !media_url) {
            const query = `
                INSERT INTO post_komun (user_ktpa, content, created_at, updated_at, media_url, media_type, komunitas_id) 
                OUTPUT INSERTED.id
                VALUES (@user_ktpa, @content, @created_at, @updated_at, NULL, NULL, @komunitas_id)
            `;

            const result = await pool.request()
                .input('user_ktpa', sql.VarChar, user_ktpa)
                .input('content', sql.Text, konten)
                .input('created_at', sql.DateTime, created_at)
                .input('updated_at', sql.DateTime, updated_at)
                .input('komunitas_id', sql.Int, komunitas_id)
                .query(query);

            if (!result.recordset || result.recordset.length === 0) {
                throw new Error('Failed to insert post without media.');
            }

            const selectQuery = `
                SELECT p.*, pe.nama 
                FROM post_komun p 
                JOIN peserta pe ON p.user_ktpa = pe.ktpa 
                WHERE p.id = @id
            `;

            const postResult = await pool.request()
                .input('id', sql.Int, result.recordset[0].id)
                .query(selectQuery);

            res.json({
                message: 'Post without media created successfully',
                post: postResult.recordset[0],
            });
        } else {
            const query = `
                INSERT INTO post_komun (user_ktpa, content, created_at, updated_at, media_url, media_type, komunitas_id) 
                OUTPUT INSERTED.id
                VALUES (@user_ktpa, @content, @created_at, @updated_at, @media_url, @media_type, @komunitas_id)
            `;

            const mediaBlob = media_url ? Buffer.from(media_url, 'base64') : null;

            const result = await pool.request()
                .input('user_ktpa', sql.VarChar, user_ktpa)
                .input('content', sql.Text, konten)
                .input('created_at', sql.DateTime, created_at)
                .input('updated_at', sql.DateTime, updated_at)
                .input('media_url', mediaBlob ? sql.VarBinary : sql.Null, mediaBlob)
                .input('media_type', sql.VarChar, media_type)
                .input('komunitas_id', sql.Int, komunitas_id)
                .query(query);

            if (!result.recordset || result.recordset.length === 0) {
                throw new Error('Failed to get the inserted post ID.');
            }

            const selectQuery = `
                SELECT p.*, pe.nama 
                FROM post_komun p 
                JOIN peserta pe ON p.user_ktpa = pe.ktpa 
                WHERE p.id = @id
            `;

            const postResult = await pool.request()
                .input('id', sql.Int, result.recordset[0].id)
                .query(selectQuery);

            res.json({
                message: 'Post created successfully',
                post: postResult.recordset[0]
            });
        }
    } catch (err) {
        console.error('Error saving post:', err);
        res.status(500).json({ error: err.message });
    }
});




app.listen(port, '0.0.0.0', () => {
    console.log(`Server running on http://localhost:${port}`);
  });
  
