import { useState, useEffect } from 'react';
import { io } from 'socket.io-client';
import Navbar from '../components/Navbar';
import '../style/Chat.css';
import logo from '../assets/logo.png';
import bumn from '../assets/BUMN.png';
import profile from '../assets/profile.jpg';

const socket = io('http://localhost:3000');

function Chat() {
    const [isNavbarActive, setIsNavbarActive] = useState(false);
    const [users, setUsers] = useState([]);
    const [filteredUsers, setFilteredUsers] = useState([]); 
    const [searchTerm, setSearchTerm] = useState(''); 
    const [selectedUser, setSelectedUser] = useState(null);
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');

    const loggedInAdmin = 'admin';

    // Fungsi untuk toggle Navbar
    const handleToggle = (isActive) => {
        setIsNavbarActive(isActive);
    };

    useEffect(() => {
        fetchUsers();

        socket.on('receive_message', (message) => {
            if (
                message.sender === selectedUser ||
                message.receiver === selectedUser
            ) {
                setMessages((prevMessages) => [...prevMessages, message]);
            }
        });

        return () => {
            socket.off('receive_message');
        };
    }, [selectedUser]);

    const fetchUsers = async () => {
        try {
            const response = await fetch('http://localhost:3000/api/users');
            const data = await response.json();
            const filtered = data.filter((user) => user !== loggedInAdmin);
            setUsers(filtered);
            setFilteredUsers(filtered); 
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };

    const fetchMessages = async (user) => {
        if (!user) return;
        try {
            const response = await fetch(
                `http://localhost:3000/api/messages?sender=${user}`
            );
            const data = await response.json();
            setMessages(data);
        } catch (error) {
            console.error('Error fetching messages:', error);
        }
    };

    const handleUserClick = (user) => {
        setSelectedUser(user);
        fetchMessages(user);
    };

    const handleSendMessage = async () => {
        if (!selectedUser || newMessage.trim() === '') return;

        const messageData = {
            message: newMessage,
            sender: loggedInAdmin,
            receiver: selectedUser,
        };

        try {
            const response = await fetch('http://localhost:3000/api/messages', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(messageData),
            });

            if (response.ok) {
                const result = await response.json();
                setMessages((prevMessages) => [
                    ...prevMessages,
                    { ...messageData, created_at: result.created_at, id: result.id },
                ]);
            }
        } catch (error) {
            console.error('Error sending message:', error);
        }

        socket.emit('send_message', messageData);

        setNewMessage('');
    };

    // Handle search input change
    const handleSearchChange = (e) => {
        const value = e.target.value.toLowerCase();
        setSearchTerm(value);
        const filtered = users.filter((user) =>
            user.toLowerCase().includes(value)
        );
        setFilteredUsers(filtered);
    };

    return (
        <div className={`layout-chat ${isNavbarActive ? 'navbar-active' : ''}`}>
            <div className={`navbar-chat ${isNavbarActive ? 'active' : ''}`}>
                <Navbar onToggle={handleToggle} />
            </div>
            <div className={`chat-content ${isNavbarActive ? 'shifted' : ''}`}>
                <div className="header-chat">
                    <img src={bumn} alt="BUMN Logo" className="header-logo-left" />
                    <h1 style={{color:"#002966"}}>Manajemen Pesan</h1>
                    <img src={logo} alt="Company Logo" className="header-logo-right" />
                </div>
                <br />
                <p style={{ color: '#fff' }}>
                    Ini adalah konten halaman chat. Lorem ipsum dolor sit amet,
                    consectetur adipiscing elit, sed do eiusmod tempor incididunt ut
                    labore et dolore magna aliqua.
                </p>

                <div className="sidebar">
                    <h2 style={{color:"#002966"}}>Pengguna</h2>
                    <div className="cari-pengguna">
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={handleSearchChange}
                            placeholder="Cari pengguna..."
                            className="search-input"
                        />
                    </div>
                    {filteredUsers.map((user, index) => (
                        <div
                            key={index}
                            className={`user-card ${user === selectedUser ? 'active' : ''}`}
                            onClick={() => handleUserClick(user)}
                        >
                            <img src={profile} alt="profile" className="profile" />
                            <span>{user}</span>
                        </div>
                    ))}
                </div>
                <div className="chat-area">
                    {selectedUser ? (
                        <>
                            <h2 style={{color:"#002966"}}>Chat dengan {selectedUser}</h2>
                            <div className="messages">
                                {messages.map((msg, index) => (
                                    <div
                                        key={index}
                                        className={`message-card ${
                                            msg.sender === loggedInAdmin
                                                ? 'outgoing'
                                                : 'incoming'
                                        }`}
                                    >
                                        <p>{msg.message}</p>
                                        <small>
                                            {new Date(msg.created_at).toLocaleString()}
                                        </small>
                                    </div>
                                ))}
                            </div>
                            <div className="input-chat">
                                <input
                                    type="text"
                                    value={newMessage}
                                    onChange={(e) => setNewMessage(e.target.value)}
                                    placeholder="Ketik pesan..."
                                />
                                <button onClick={handleSendMessage}>Kirim</button>
                            </div>
                        </>
                    ) : (
                        <p style={{color:"#002966"}}>Pilih pengguna untuk melihat pesan.</p>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Chat;
