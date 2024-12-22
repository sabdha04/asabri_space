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
const port = 3000;

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


// Increase payload size limit
app.use(express.json({limit: '100mb'}));
app.use(express.urlencoded({limit: '100mb', extended: true}));
app.use(express.raw({limit: '50mb'}));

// Konfigurasi koneksi MySQL
const db = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '', // Sesuaikan dengan password MySQL Anda
    database: 'medsos',
    charset: 'utf8mb4'
});

// Add these configurations
db.query("SET NAMES utf8mb4;");
db.query("SET CHARACTER SET utf8mb4;");
db.query("SET character_set_connection=utf8mb4;");

// Koneksi ke database
db.getConnection((err) => {
    if (err) {
        console.error('Error connecting to database:', err);
        return;
    }
    console.log('Connected to MySQL database');
});

// API Endpoints
// Get semua staff
app.get('/api/staff', (req, res) => {
    const query = 'SELECT * FROM staff';
    db.query(query, (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json(results);
    });
});
app.get('/api/peserta', (req, res) => {
    const query = 'SELECT * FROM peserta where ktpa = ?';
    db.query(query, (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json(results);
    });
});


// Get staff by ID
app.get('/api/staff/:id', (req, res) => {
    const query = 'SELECT * FROM staff WHERE id = ?';
    db.query(query, [req.params.id], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json(results[0]);
    });
});

// Tambah staff baru
app.post('/api/staff', (req, res) => {
    const { username, password, last_login } = req.body;
    const query = 'INSERT INTO staff (username, password, last_Login) VALUES (?, ?, ?)';
    db.query(query, [username, password, last_login], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json({ id: results.insertId, message: 'Staff added successfully' });
    });
});

// Global object to store chunks temporarily
const videoChunks = {};

app.post('/api/post', (req, res) => {
    // Add content type header
    res.setHeader('Content-Type', 'application/json; charset=utf8mb4');
    
    const { ktpa, content, created_at, updated_at, media_url, media_type } = req.body;
    
    // Check if this is a chunked upload
    if (media_type && media_type.includes('video_chunk')) {
        const [_, chunkIndex, totalChunks] = media_type.match(/video_chunk_(\d+)_of_(\d+)/);
        
        // Initialize array for this upload if doesn't exist
        if (!videoChunks[ktpa]) {
            videoChunks[ktpa] = {
                chunks: [],
                content,
                created_at,
                updated_at
            };
        }
        
        // Add this chunk
        videoChunks[ktpa].chunks[parseInt(chunkIndex)] = media_url;
        
        // Check if we have all chunks
        if (videoChunks[ktpa].chunks.filter(Boolean).length === parseInt(totalChunks)) {
            // Combine all chunks
            const completeVideo = videoChunks[ktpa].chunks.join('');
            
            // Create the post with complete video
            const query = 'INSERT INTO post (ktpa, content, created_at, updated_at, media_url, media_type) VALUES (?, ?, ?, ?, ?, ?)';
            const mediaBlob = Buffer.from(completeVideo, 'base64');
            
            db.query(query, [
                ktpa, 
                videoChunks[ktpa].content, 
                videoChunks[ktpa].created_at, 
                videoChunks[ktpa].updated_at, 
                mediaBlob, 
                'video'
            ], (err, results) => {
                if (err) {
                    console.error('Error saving post:', err);
                    res.status(500).json({ error: err.message });
                    return;
                }
                
                // Clean up chunks
                delete videoChunks[ktpa];
                
                // Get the inserted post with user info
                const selectQuery = `
                    SELECT p.*, pe.nama 
                    FROM post p 
                    JOIN peserta pe ON p.ktpa = pe.ktpa 
                    WHERE p.id = ?
                `;
                
                db.query(selectQuery, [results.insertId], (err, post) => {
                    if (err) {
                        res.status(500).json({ error: err.message });
                        return;
                    }
                    
                    if (post.length > 0) {
                        const postData = post[0];
                        if (postData.media_url) {
                            postData.media_url = postData.media_url.toString('base64');
                        }
                        res.json(postData);
                    } else {
                        res.status(404).json({ message: 'Post not found after creation' });
                    }
                });
            });
        } else {
            // Not all chunks received yet
            res.json({ message: `Chunk ${parseInt(chunkIndex) + 1} of ${totalChunks} received` });
        }
    } else {
        // Handle non-chunked uploads as before
        const query = `
            INSERT INTO post (ktpa, content, created_at, updated_at, media_url, media_type) 
            VALUES (?, ?, ?, ?, ?, ?)
        `;
        
        db.query(query, [
            ktpa,
            content,
            created_at,
            updated_at,
            media_url ? Buffer.from(media_url, 'base64') : null,
            media_type
        ], (err, results) => {
            if (err) {
                console.error('Error saving post:', err);
                res.status(500).json({ error: err.message });
                return;
            }
            
            // Get the inserted post with user info
            const selectQuery = `
                SELECT p.*, pe.nama 
                FROM post p 
                JOIN peserta pe ON p.ktpa = pe.ktpa 
                WHERE p.id = ?
            `;
            
            db.query(selectQuery, [results.insertId], (err, post) => {
                if (err) {
                    res.status(500).json({ error: err.message });
                    return;
                }
                
                if (post.length > 0) {
                    const postData = post[0];
                    if (postData.media_url) {
                        postData.media_url = postData.media_url.toString('base64');
                    }
                    res.json(postData);
                } else {
                    res.status(404).json({ message: 'Post not found after creation' });
                }
            });
        });
    }
});

//login staff
app.post('/api/staff/login', (req, res) => {
    console.log('Login request received:', req.body);
    
    const { username, password } = req.body;
    
    const query = 'SELECT * FROM staff WHERE username = ? AND password = ?';
    db.query(query, [username, password], (err, results) => {
        if (err) {
            console.error('Database error:', err);
            res.status(500).json({ 
                success: false, 
                message: err.message 
            });
            return;
        }

        console.log('Query results:', results);

        if (results.length > 0) {
            const staff = results[0];
            delete staff.password;
            
            res.json({
                success: true,
                message: 'Login successful',
                staff: staff
            });
        } else {
            res.json({
                success: false,
                message: 'Invalid username or password'
            });
        }
    });
});

//login peserta
app.post('/api/peserta/login', (req, res) => {
    console.log('Login request received:', req.body);
    
    const { ktpa, password } = req.body;
    
    const query = 'SELECT * FROM peserta WHERE ktpa = ? and password = ?';
    db.query(query, [ktpa, password], (err, results) => {
        if (err) {
            console.error('Database error:', err);
            res.status(500).json({ 
                success: false, 
                message: err.message 
            });
            return;
        }

        console.log('Query results:', results);

        if (results.length > 0) {
            const peserta = results[0];
            delete peserta.password;
            
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
    });
});

// Update staff
app.put('/api/staff/:id', (req, res) => {
    const { username, password, last_login } = req.body;
    const query = 'UPDATE staff SET username = ?, password = ?, last_login = ? WHERE id = ?';
    db.query(query, [username, password, last_login.id], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json({ message: 'Staff updated successfully' });
    });
});

// Delete staff
app.delete('/api/staff/:id', (req, res) => {
    const query = 'DELETE FROM staff WHERE id = ?';
    db.query(query, [req.params.id], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json({ message: 'Staff deleted successfully' });
    });
});

// Get all posts
app.get('/api/posts', (req, res) => {
    const query = `
        SELECT p.*, pe.nama,
        (SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count
        FROM post p 
        JOIN peserta pe ON p.ktpa = pe.ktpa 
        ORDER BY p.created_at DESC
    `;
    
    db.query(query, (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        const posts = results.map(post => {
            if (post.media_url) {
                post.media_url = post.media_url.toString('base64');
            }
            return {
                ...post,
                comment_count: parseInt(post.comment_count) || 0
            };
        });
        
        res.json(posts);
    });
});

// Get single post
app.get('/api/posts/:id', (req, res) => {
    const query = `
        SELECT p.*, pe.nama,
        (SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count
        FROM post p 
        JOIN peserta pe ON p.ktpa = pe.ktpa 
        WHERE p.id = ?
    `;
    
    db.query(query, [req.params.id], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        if (results.length > 0) {
            const post = results[0];
            if (post.media_url) {
                post.media_url = post.media_url.toString('base64');
            }
            post.comment_count = parseInt(post.comment_count) || 0;
            res.json(post);
        } else {
            res.status(404).json({ message: 'Post not found' });
        }
    });
});

// Get comments for a specific post
app.get('/api/posts/:postId/comments', (req, res) => {
    const query = `
        SELECT c.*, p.nama 
        FROM comment c
        JOIN peserta p ON c.ktpa = p.ktpa
        WHERE c.post_id = ?
        ORDER BY c.created_at DESC
    `;
    
    db.query(query, [req.params.postId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json(results);
    });
});

// Create a new comment
app.post('/api/comments', (req, res) => {
    const { post_id, ktpa, content } = req.body;
    const created_at = new Date().toISOString().slice(0, 19).replace('T', ' ');
    const updated_at = created_at;

    const query = 'INSERT INTO comment (post_id, ktpa, content, created_at, updated_at) VALUES (?, ?, ?, ?, ?)';
    
    db.query(query, [post_id, ktpa, content, created_at, updated_at], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        // Get updated comment count
        const countQuery = 'SELECT COUNT(*) as count FROM comment WHERE post_id = ?';
        db.query(countQuery, [post_id], (err, countResults) => {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            
            const comment_count = countResults[0].count;
            
            // Return the new comment with count
            const selectQuery = `
                SELECT c.*, p.nama 
                FROM comment c
                JOIN peserta p ON c.ktpa = p.ktpa
                WHERE c.id = ?
            `;
            
            db.query(selectQuery, [results.insertId], (err, commentResults) => {
                if (err) {
                    res.status(500).json({ error: err.message });
                    return;
                }
                
                res.json({
                    message: 'Comment created successfully',
                    comment: commentResults[0],
                    comment_count: comment_count
                });
            });
        });
    });
});

// Delete a comment
app.delete('/api/comments/:id', (req, res) => {
    const query = 'DELETE FROM comment WHERE id = ?';
    
    db.query(query, [req.params.id], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json({ message: 'Comment deleted successfully' });
    });
});

// Toggle like (like/unlike)
app.post('/api/posts/:postId/like', (req, res) => {
    const { ktpa } = req.body;
    const postId = req.params.postId;
    const created_at = new Date().toISOString().slice(0, 19).replace('T', ' ');

    // Check if like exists
    const checkQuery = 'SELECT id FROM likes WHERE post_id = ? AND ktpa = ?';
    db.query(checkQuery, [postId, ktpa], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }

        if (results.length > 0) {
            // Unlike: Remove existing like
            const deleteQuery = 'DELETE FROM likes WHERE post_id = ? AND ktpa = ?';
            db.query(deleteQuery, [postId, ktpa], (err, deleteResult) => {
                if (err) {
                    res.status(500).json({ error: err.message });
                    return;
                }
                res.json({ liked: false, message: 'Post unliked successfully' });
            });
        } else {
            // Like: Add new like
            const insertQuery = 'INSERT INTO likes (post_id, ktpa, created_at) VALUES (?, ?, ?)';
            db.query(insertQuery, [postId, ktpa, created_at], (err, insertResult) => {
                if (err) {
                    res.status(500).json({ error: err.message });
                    return;
                }
                res.json({ liked: true, message: 'Post liked successfully' });
            });
        }
    });
});

// Get like count and user's like status for a post
// app.get('/api/posts/:postId/likes', (req, res) => {
//     const postId = req.params.postId;
//     const ktpa = req.query.ktpa;

//     const queries = [
//         'SELECT COUNT(*) as likeCount FROM likes WHERE post_id = ?',
//         'SELECT EXISTS(SELECT 1 FROM likes WHERE post_id = ? AND ktpa = ?) as userLiked'
//     ];

//     db.query(queries.join(';'), [postId, postId, ktpa], (err, results) => {
//         if (err) {
//             res.status(500).json({ error: err.message });
//             return;
//         }

//         res.json({
//             likeCount: results[0][0].likeCount,
//             userLiked: results[1][0].userLiked === 1
//         });
//     });
// });

// Get like count for a specific post
app.get('/api/posts/:postId/likes', (req, res) => {
    const postId = req.params.postId;

    const query = 'SELECT COUNT(*) as likeCount FROM likes WHERE post_id = ?';
    db.query(query, [postId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }

        res.json({
            likeCount: results[0].likeCount
        });
    });
});

// Update post
app.put('/api/posts/:id', (req, res) => {
    const postId = req.params.id;
    const { content } = req.body;
    const updated_at = new Date().toISOString().slice(0, 19).replace('T', ' ');

    const query = 'UPDATE post SET content = ?, updated_at = ? WHERE id = ?';
    
    db.query(query, [content, updated_at, postId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        if (results.affectedRows === 0) {
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
    });
});

// Delete all likes for a specific post
app.delete('/api/posts/:postId/likes', (req, res) => {
    const postId = req.params.postId;
    
    const query = 'DELETE FROM likes WHERE post_id = ?';
    db.query(query, [postId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json({ message: 'All likes deleted successfully' });
    });
});

// Delete all comments for a specific post
app.delete('/api/posts/:postId/comments', (req, res) => {
    const postId = req.params.postId;
    
    const query = 'DELETE FROM comment WHERE post_id = ?';
    db.query(query, [postId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json({ message: 'All comments deleted successfully' });
    });
});
// Mendapatkan semua berita
app.get('/news', (req, res) => {
    db.query('SELECT * FROM news', (err, results) => {
      if (err) {
        return res.status(500).json({ error: err.message });
      }
      res.json(results);
    });
  });
  
  // Menambahkan berita baru
  app.post('/news', (req, res) => {
    const { title, description } = req.body;
    db.query('INSERT INTO news (title, description) VALUES (?, ?)', [title, description], (err, results) => {
      if (err) {
        return res.status(500).json({ error: err.message });
      }
      res.status(201).json({ id: results.insertId, title, description });
    });
  });
  
  // Memperbarui berita
  app.put('/news/:id', (req, res) => {
    const { id } = req.params;
    const { title, description } = req.body;
    db.query('UPDATE news SET title = ?, description = ? WHERE id = ?', [title, description, id], (err) => {
      if (err) {
        return res.status(500).json({ error: err.message });
      }
      res.json({ id, title, description });
    });
  });
  
  // Menghapus berita
  app.delete('/news/:id', (req, res) => {
    const { id } = req.params;
    db.query('DELETE FROM news WHERE id = ?', [id], (err) => {
      if (err) {
        return res.status(500).json({ error: err.message });
      }
      res.status(204).send();
    });
  });
  

// Update delete post endpoint to handle cascading deletes
app.delete('/api/posts/:id', (req, res) => {
    const postId = req.params.id;
    
    // First delete likes
    const deleteLikesQuery = 'DELETE FROM likes WHERE post_id = ?';
    db.query(deleteLikesQuery, [postId], (err) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        // Then delete comments
        const deleteCommentsQuery = 'DELETE FROM comment WHERE post_id = ?';
        db.query(deleteCommentsQuery, [postId], (err) => {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            
            // Finally delete the post
            const deletePostQuery = 'DELETE FROM post WHERE id = ?';
            db.query(deletePostQuery, [postId], (err, results) => {
                if (err) {
                    res.status(500).json({ error: err.message });
                    return;
                }
                
                if (results.affectedRows === 0) {
                    res.status(404).json({ message: 'Post not found' });
                    return;
                }
                
                res.json({ message: 'Post and all related data deleted successfully' });
            });
        });
    });
});

// Get all news
app.get('/api/news', (req, res) => {
    const query = `
        SELECT id, title, description, date, created_at, updated_at, image, author
        FROM news
        ORDER BY created_at DESC
    `;
    
    db.query(query, (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        res.json(results);
    });
});

// Get single news by id
app.get('/api/news/:id', (req, res) => {
    const query = `
        SELECT id, title, description, date, created_at, updated_at, image,author
        FROM news
        WHERE id = ?
    `;
    
    db.query(query, [req.params.id], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        if (results.length > 0) {
            res.json(results[0]);
        } else {
            res.status(404).json({ message: 'News not found' });
        }
    });
});

// Mendapatkan semua berita
app.get('/news', (req, res) => {
    db.query('SELECT * FROM news', (err, results) => {
      if (err) {
        return res.status(500).json({ error: err.message });
      }
      res.json(results);
    });
  });
  
  // Menambahkan berita baru dengan gambar
  app.post('/news', (req, res) => {
    const { date, title, description, image, author } = req.body;
    db.query(
      'INSERT INTO news (date, title, description, image, author) VALUES (?, ?, ?, ?, ?)', 
      [date, title, description, image, author], 
      (err, results) => {
        if (err) {
          return res.status(500).json({ error: err.message });
        }
        res.status(201).json({ id: results.insertId, date, title, description, image, author });
      }
    );
  });
  
  // Memperbarui berita
  app.put('/news/:id', (req, res) => {
    const { id } = req.params;
    const { date, title, description } = req.body;
    db.query('UPDATE news SET date = ?, title = ?, description = ? WHERE id = ?', 
      [date, title, description, id], 
      (err) => {
        if (err) {
          return res.status(500).json({ error: err.message });
        }
        res.json({ id, date, title, description });
      }
    );
  });
  
  // Menghapus berita
  app.delete('/news/:id', (req, res) => {
    const { id } = req.params;
    db.query('DELETE FROM news WHERE id = ?', [id], (err) => {
      if (err) {
        return res.status(500).json({ error: err.message });
      }
      res.status(204).send();
    });
  });

// Update block status for a post
app.put('/api/posts/:postId/block', (req, res) => {
    const postId = req.params.postId;
    const { block_status } = req.body;
    
    const query = 'UPDATE post SET blok = ? WHERE id = ?';
    db.query(query, [block_status, postId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json({ message: 'Block status updated successfully' });
    });
});

// Tambahkan endpoint untuk pendaftaran peserta
app.post('/api/peserta/register', (req, res) => {
    const { ktpa, nama, jenis_kelamin, password, email } = req.body;
    const created_at = new Date().toISOString().slice(0, 19).replace('T', ' ');
    const query = 'INSERT INTO peserta (ktpa, nama, jenis_kelamin, password, created_at, updated_at, level, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)';
    
    db.query(query, [ktpa, nama, jenis_kelamin, password, created_at, created_at, 'peserta', email], (err, results) => {
        if (err) {
            console.error('Database error:', err);
            res.status(500).json({ error: err.message });
            return;
        }
        res.status(201).json({ message: 'Pendaftaran berhasil' });
    });
});
// Tambahkan endpoint untuk memperbarui data peserta
app.put('/api/peserta/update', (req, res) => {
    const { ktpa, nama, email, jenis_kelamin, bio } = req.body;

    // Validasi input
    if (!ktpa || !nama || !email) {
        return res.status(400).json({ error: 'KTPA, nama, dan email harus diisi' });
    }

    const query = 'UPDATE peserta SET nama = ?, email = ?, jenis_kelamin = ?, bio = ? WHERE ktpa = ?';
    
    db.query(query, [nama, email, jenis_kelamin, bio, ktpa], (err, results) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).json({ error: err.message });
        }
        if (results.affectedRows === 0) {
            return res.status(404).json({ error: 'Peserta tidak ditemukan' });
        }
        res.json({ message: 'Data peserta berhasil diperbarui' });
    });
});

//post Event
app.post('/api/postev', upload.single('image'), (req, res) => {
    console.log('Received data:', req.body); 
    const image = req.file;
    const { ktpa, title, description, date, loc, kuota } = req.body;
    if (!title || !description) {
        console.error('Invalid data received');
        return res.status(400).send('Title and Description are required');
    }

    const imageBase64 = image.buffer.toString('base64');
    const formatDate = (date) => {
        const d = new Date(date);
        return d.toISOString(); // Menghasilkan format 'yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
    };

    // const formattedDate = formatDate(new Date());
    const formattedDate = formatDate(date);
    const created_at = new Date();
    // const mediaUrl = image.buffer.toString('base64');
    const query = 'INSERT INTO event (ktpa, evtitle, evdesc, evdate, evloc, evkuota, eventpic, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)';
    db.query(query, [
        ktpa,
        title, 
        description,
        date,
        loc,
        kuota,
        image.buffer,
        created_at], (err, result) => {
        if (err) {
            console.error('Database error:', err);
            return res.status(500).send('Database error');
        }

        // notifikasi
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
                // event_pic_uri: imageBase64, 
            },
            topic: 'all',
        };

        admin.messaging().send(message)
            .then((response) => {
                console.log('Successfully sent message:', response);
                res.status(200).send('Post created and notification sent');
            })
            .catch((error) => {
                console.error('Error sending notification:', error);
                res.status(500).send('Notification error');
            });
    });
});

app.get('/api/getev', (req, res) => {
    const query = 'SELECT * FROM event ORDER BY created_at DESC'; 

    db.query(query, (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        const posts = results.map(post => {
            if (post.media_url) {
                post.media_url = post.media_url.toString('base64');
            }
            return post;
        });
        
        res.json(posts);
    });

});

app.get('/api/komunitas', (req, res) => {
    db.query('SELECT * FROM komunitas', (err, result) => {
        if (err) throw err;
        res.json(result);
    });
});

app.post('/api/join-komunitas', (req, res) => {
    const { user_ktpa, komunitas_id } = req.body;
    console.log('Received data:', req.body); 

    db.query('SELECT * FROM anggota_komunitas WHERE komunitas_id = ? AND user_ktpa = ?', [komunitas_id, user_ktpa], (err, result) => {
        if (err) throw err;
        if (result.length > 0) {
            return res.status(400).json({ success: false, message: 'Pengguna sudah bergabung dengan komunitas ini' });
        }

        db.query('INSERT INTO anggota_komunitas (komunitas_id, user_ktpa) VALUES (?, ?)', [komunitas_id, user_ktpa], (err) => {
            if (err) throw err;
            res.status(200).json({ success: true, message: 'Berhasil bergabung dengan komunitas' });
        });
    });
});

app.post('/api/unjoin-komunitas', (req, res) => {
    const { user_ktpa, komunitas_id } = req.body;
    console.log('Received data:', req.body); 

    db.query('SELECT * FROM anggota_komunitas WHERE komunitas_id = ? AND user_ktpa = ?', [komunitas_id, user_ktpa], (err, result) => {
        if (err) throw err;
        if (result.length === 0) {
            return res.status(400).json({ success: false, message: 'Pengguna belum bergabung dengan komunitas ini' });
        }

        db.query('DELETE FROM anggota_komunitas WHERE komunitas_id = ? AND user_ktpa = ?', [komunitas_id, user_ktpa], (err) => {
            if (err) throw err;
            res.status(200).json({ success: true, message: 'Berhasil meninggalkan komunitas' });
        });
    });
});

app.post('/api/is-user-joined', (req, res) => {
    const { user_ktpa, komunitas_id } = req.body;

    console.log('Checking join status for:', user_ktpa, 'in komunitas', komunitas_id);

    const query = 'SELECT * FROM anggota_komunitas WHERE komunitas_id = ? AND user_ktpa = ?';
    db.query(query, [komunitas_id, user_ktpa], (err, result) => {
        if (err) {
            console.error('Error executing query:', err);
            return res.status(500).json({ success: false, message: 'Internal Server Error' });
        }

        if (result.length > 0) {
            // Pengguna sudah bergabung
            return res.status(200).json(true);
        } else {
            // Pengguna belum bergabung
            return res.status(200).json(false);
        }
    });
});

// untuk meninggalkan komunitas
app.post('/api/leave-komunitas', (req, res) => {
    const { user_ktpa, komunitas_id } = req.body;

    console.log('Leaving community for:', user_ktpa, 'in komunitas', komunitas_id);

    const checkQuery = 'SELECT * FROM anggota_komunitas WHERE komunitas_id = ? AND user_ktpa = ?';
    db.query(checkQuery, [komunitas_id, user_ktpa], (err, result) => {
        if (err) {
            console.error('Error executing check query:', err);
            return res.status(500).json({ success: false, message: 'Internal Server Error' });
        }

        if (result.length === 0) {
            return res.status(400).json({ success: false, message: 'Pengguna belum bergabung dengan komunitas ini' });
        }

        const deleteQuery = 'DELETE FROM anggota_komunitas WHERE komunitas_id = ? AND user_ktpa = ?';
        db.query(deleteQuery, [komunitas_id, user_ktpa], (err) => {
            if (err) {
                console.error('Error executing delete query:', err);
                return res.status(500).json({ success: false, message: 'Internal Server Error' });
            }

            return res.status(200).json({ success: true, message: 'Berhasil meninggalkan komunitas' });
        });
    });
});

app.post('/api/buat-post', (req, res) => {
    const { user_ktpa, komunitas_id, konten, media_url, media_type, created_at, updated_at } = req.body;

    // Check if this is a chunked upload
    if (media_type && media_type.includes('video_chunk')) {
        const [_, chunkIndex, totalChunks] = media_type.match(/video_chunk_(\d+)_of_(\d+)/);

        // Initialize array for this upload if doesn't exist
        if (!videoChunks[user_ktpa]) {
            videoChunks[user_ktpa] = {
                chunks: [],
                content: konten,
                created_at,
                updated_at,
                komunitas_id
            };
        }

        // Add this chunk
        videoChunks[user_ktpa].chunks[parseInt(chunkIndex)] = media_url;

        // Check if we have all chunks
        if (videoChunks[user_ktpa].chunks.filter(Boolean).length === parseInt(totalChunks)) {
            // Combine all chunks
            const completeVideo = videoChunks[user_ktpa].chunks.join('');

            // Insert the post with the full video
            const query = `
                INSERT INTO post_komun (komunitas_id, user_ktpa, content, media_url, media_type, created_at, updated_at) 
                VALUES (?, ?, ?, ?, ?, ?, ?)
            `;
            const mediaBlob = Buffer.from(completeVideo, 'base64');

            db.query(query, [
                videoChunks[user_ktpa].komunitas_id,
                user_ktpa,
                videoChunks[user_ktpa].content,
                mediaBlob,
                'video',
                videoChunks[user_ktpa].created_at,
                videoChunks[user_ktpa].updated_at
            ], (err, results) => {
                if (err) {
                    console.error('Error saving post:', err);
                    res.status(500).json({ error: err.message });
                    return;
                }

                // Clean up chunks after successful upload
                delete videoChunks[user_ktpa];

                // Return the created post data
                const selectQuery = `
                    SELECT p.*, pe.nama 
                    FROM post_komun p 
                    JOIN peserta pe ON p.user_ktpa = pe.ktpa 
                    WHERE p.id = ?
                `;
                db.query(selectQuery, [results.insertId], (err, post) => {
                    if (err) {
                        res.status(500).json({ error: err.message });
                        return;
                    }

                    if (post.length > 0) {
                        const postData = post[0];
                        if (postData.media_url) {
                            postData.media_url = postData.media_url.toString('base64');
                        }
                        res.json(postData);
                    } else {
                        res.status(404).json({ message: 'Post not found after creation' });
                    }
                });
            });
        } else {
            // Not all chunks received yet
            res.json({ message: `Chunk ${parseInt(chunkIndex) + 1} of ${totalChunks} received` });
        }
    } else {
        // Handle non-chunked uploads
        db.query('SELECT * FROM anggota_komunitas WHERE komunitas_id = ? AND user_ktpa = ?', [komunitas_id, user_ktpa], (err, result) => {
            if (err) throw err;
            if (result.length === 0) {
                return res.status(400).json({ message: 'Pengguna harus bergabung terlebih dahulu untuk membuat postingan' });
            }

            // Handle non-chunked post
            const query = `
                INSERT INTO post_komun (komunitas_id, user_ktpa, content, media_url, media_type, created_at, updated_at) 
                VALUES (?, ?, ?, ?, ?, ?, ?)
            `;

            db.query(query, [
                komunitas_id,
                user_ktpa,
                konten,
                media_url ? Buffer.from(media_url, 'base64') : null,
                media_type,
                created_at,
                updated_at
            ], (err, results) => {
                if (err) throw err;

                // Return the created post data
                const selectQuery = `
                    SELECT p.*, pe.nama 
                    FROM post_komun p 
                    JOIN peserta pe ON p.user_ktpa = pe.ktpa 
                    WHERE p.id = ?
                `;
                db.query(selectQuery, [results.insertId], (err, post) => {
                    if (err) {
                        res.status(500).json({ error: err.message });
                        return;
                    }

                    if (post.length > 0) {
                        const postData = post[0];
                        if (postData.media_url) {
                            postData.media_url = postData.media_url.toString('base64');
                        }
                        res.json(postData);
                    } else {
                        res.status(404).json({ message: 'Post not found after creation' });
                    }
                });
            });
        });
    }
});

// app.get('/api/getpkomun', (req, res) => {
//     const query = `
//         SELECT p.*, pe.nama,
//         (SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count
//         FROM post_komun p 
//         JOIN peserta pe ON p.user_ktpa = pe.ktpa 
//         ORDER BY p.created_at DESC
//     `;
    
//     db.query(query, (err, results) => {
//         if (err) {
//             res.status(500).json({ error: err.message });
//             return;
//         }
        
//         const posts = results.map(post => {
//             if (post.media_url) {
//                 post.media_url = post.media_url.toString('base64');
//             }
//             return {
//                 ...post,
//                 comment_count: parseInt(post.comment_count) || 0
//             };
//         });
        
//         res.json(posts);
//     });
// });

app.get('/api/getpkomun', (req, res) => {
    const komunitasId = req.query.komunitasId;
    
    if (!komunitasId) {
        return res.status(400).json({ error: 'komunitasId is required' });
    }

    const query = `
        SELECT p.*, pe.nama,
        (SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count
        FROM post_komun p
        JOIN peserta pe ON p.user_ktpa = pe.ktpa
        WHERE p.komunitas_id = ?  
        ORDER BY p.created_at DESC
    `;
    
    db.query(query, [komunitasId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        const posts = results.map(post => {
            if (post.media_url) {
                post.media_url = post.media_url.toString('base64');
            }
            return {
                ...post,
                comment_count: parseInt(post.comment_count) || 0
            };
        });
        
        res.json(posts);
    });
});

app.get('/api/getpkomun/:id', (req, res) => {
    const komunitasId = req.query.komunitasId;  
    // const komunitasId = '2';
    const postId = req.params.id;  

    if (!komunitasId) {
        return res.status(400).json({ error: 'komunitasId is required' });
    }

    const query = `
        SELECT p.*, pe.nama,
        (SELECT COUNT(*) FROM comment WHERE post_id = p.id) as comment_count
        FROM post_komun p
        JOIN peserta pe ON p.user_ktpa = pe.ktpa
        WHERE p.id = ? AND p.komunitas_id = ?
    `;
    
    db.query(query, [postId, komunitasId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        if (results.length > 0) {
            const post = results[0];
            if (post.media_url) {
                post.media_url = post.media_url.toString('base64');  // Convert media URL to base64
            }
            post.comment_count = parseInt(post.comment_count) || 0;  // Set comment count, default to 0 if null
            res.json(post);  // Return the post data
        } else {
            res.status(404).json({ message: 'Post not found' });  // Return 404 if post not found
        }
    });
});

app.get('/api/getpkomun/:postId/comments', (req, res) => {
    const komunitasId = req.query.komunitasId;  
    // const komunitasId = '2';
    const postId = req.params.postId;  

    if (!komunitasId) {
        return res.status(400).json({ error: 'komunitasId is required' });
    }
    const query = `
        SELECT c.*, p.nama 
        FROM comment_komun c
        JOIN peserta p ON c.ktpa = p.ktpa
        WHERE c.post_id = ?
        ORDER BY c.created_at DESC
    `;
    
    db.query(query, [postId, komunitasId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json(results);
    });
});

app.post('/api/commentskomun', (req, res) => {
    const { post_id, ktpa, content } = req.body;
    const created_at = new Date().toISOString().slice(0, 19).replace('T', ' ');
    const updated_at = created_at;
    const komunitas_id = req.query.komunitasId;  

    // Check if the user is a member of the community
    const checkMembershipQuery = 'SELECT COUNT(*) AS count FROM anggota_komunitas WHERE komunitas_id = ? AND user_ktpa = ?';
    db.query(checkMembershipQuery, [komunitas_id, ktpa], (err, membershipResults) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }

        if (membershipResults[0].count === 0) {
            return res.status(403).json({ error: 'Anda harus bergabung ke komunitas untuk membuat komentar.' });
        }

        const query = 'INSERT INTO comment_komun (post_id, komunitas_id, ktpa, content, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)';
        db.query(query, [post_id, komunitas_id, ktpa, content, created_at, updated_at], (err, results) => {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            
            const countQuery = 'SELECT COUNT(*) as count FROM comment WHERE post_id = ?';
            db.query(countQuery, [post_id], (err, countResults) => {
                if (err) {
                    res.status(500).json({ error: err.message });
                    return;
                }
                
                const comment_count = countResults[0].count;
                
                // Return the new comment with count
                const selectQuery = `
                    SELECT c.*, p.nama 
                    FROM comment_komun c
                    JOIN peserta p ON c.ktpa = p.ktpa
                    WHERE c.id = ?
                `;
                
                db.query(selectQuery, [results.insertId], (err, commentResults) => {
                    if (err) {
                        res.status(500).json({ error: err.message });
                        return;
                    }
                    
                    res.json({
                        message: 'Comment created successfully',
                        comment: commentResults[0],
                        comment_count: comment_count
                    });
                });
            });
        });
    });
});

app.post('/api/getpkomun/:postId/like', (req, res) => {
    const { ktpa, komunitasId } = req.body;  
    const postId = req.params.postId;
    const created_at = new Date().toISOString().slice(0, 19).replace('T', ' ');

    if (!komunitasId || !ktpa) {
        return res.status(400).json({ error: 'komunitasId and ktpa are required' });
    }

    const checkQuery = 'SELECT id FROM likes_komun WHERE post_id = ? AND ktpa = ? AND komunitas_id = ?';
    db.query(checkQuery, [postId, ktpa, komunitasId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }

        if (results.length > 0) {
            // un-like
            const deleteQuery = 'DELETE FROM likes_komun WHERE post_id = ? AND ktpa = ? AND komunitas_id = ?';
            db.query(deleteQuery, [postId, ktpa, komunitasId], (err, deleteResult) => {
                if (err) {
                    res.status(500).json({ error: err.message });
                    return;
                }
                res.json({ liked: false, message: 'Post unliked successfully' });
            });
        } else {
            // like
            const insertQuery = 'INSERT INTO likes_komun (post_id, komunitas_id, ktpa, created_at) VALUES (?, ?, ?, ?)';
            db.query(insertQuery, [postId, komunitasId, ktpa, created_at], (err, insertResult) => {
                if (err) {
                    res.status(500).json({ error: err.message });
                    return;
                }
                res.json({ liked: true, message: 'Post liked successfully' });
            });
        }
    });
});

app.get('/api/getpkomun/:postId/likes', (req, res) => {
    const postId = req.params.postId;
    const query = `
        SELECT COUNT(*) AS likeCount 
        FROM likes_komun 
        WHERE post_id = ?`;
    db.query(query, [postId], (err, results) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json({ likeCount: results[0].likeCount });
    });
});

//update post komun
app.put('/api/getpkomun/:id', (req, res) => {
    const postId = req.params.id;
    const { content } = req.body;
    const updated_at = new Date().toISOString().slice(0, 19).replace('T', ' ');

    const query = 'UPDATE post_komun SET content = ?, updated_at = ? WHERE id = ?';
    
    db.query(query, [content, updated_at, postId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        if (results.affectedRows === 0) {
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
    });
});

app.delete('/api/getpkomun/:id', (req, res) => {
    const postId = req.params.id;
    
    // First delete likes
    const deleteLikesQuery = 'DELETE FROM likes_komun WHERE post_id = ?';
    db.query(deleteLikesQuery, [postId], (err) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        
        // Then delete comments
        const deleteCommentsQuery = 'DELETE FROM comment_komun WHERE post_id = ?';
        db.query(deleteCommentsQuery, [postId], (err) => {
            if (err) {
                res.status(500).json({ error: err.message });
                return;
            }
            
            // Finally delete the post
            const deletePostQuery = 'DELETE FROM post_komun WHERE id = ?';
            db.query(deletePostQuery, [postId], (err, results) => {
                if (err) {
                    res.status(500).json({ error: err.message });
                    return;
                }
                
                if (results.affectedRows === 0) {
                    res.status(404).json({ message: 'Post not found' });
                    return;
                }
                
                res.json({ message: 'Post and all related data deleted successfully' });
            });
        });
    });
});

// Delete all likes for a specific post
app.delete('/api/getpkomun/:postId/likes', (req, res) => {
    const postId = req.params.postId;
    
    const query = 'DELETE FROM likes WHERE post_id = ?';
    db.query(query, [postId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json({ message: 'All likes deleted successfully' });
    });
});

// Delete all comments for a specific post
app.delete('/api/getpkomun/:postId/comments', (req, res) => {
    const postId = req.params.postId;
    
    const query = 'DELETE FROM comment WHERE post_id = ?';
    db.query(query, [postId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }
        res.json({ message: 'All comments deleted successfully' });
    });
});


app.get('/api/getpkomun/:postId/likes', (req, res) => {
    const postId = req.params.postId;

    const query = 'SELECT COUNT(*) as likeCount FROM likes_komun WHERE post_id = ?';
    db.query(query, [postId], (err, results) => {
        if (err) {
            res.status(500).json({ error: err.message });
            return;
        }

        res.json({
            likeCount: results[0].likeCount
        });
    });
});


app.listen(3000, '0.0.0.0', () => {
    console.log(`Server running on port ${port}`);
});