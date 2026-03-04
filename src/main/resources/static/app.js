document.addEventListener('DOMContentLoaded', () => {
    loadUsers();
    loadBooks();
});

// User Management
async function loadUsers() {
    try {
        const response = await fetch('/api/users');
        const users = await response.json();
        const userTableBody = document.querySelector('#userTable tbody');
        userTableBody.innerHTML = '';
        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.role}</td>
            `;
            userTableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading users:', error);
    }
}

async function registerUser() {
    const name = document.getElementById('userName').value;
    const role = document.getElementById('userRole').value;
    if (!name) {
        alert('이름을 입력해주세요.');
        return;
    }

    try {
        const response = await fetch(`/api/users/register?name=${encodeURIComponent(name)}&role=${encodeURIComponent(role)}`, {
            method: 'POST'
        });
        if (response.ok) {
            alert('회원 등록 성공!');
            loadUsers();
            document.getElementById('userName').value = '';
        } else {
            alert('회원 등록 실패');
        }
    } catch (error) {
        console.error('Error registering user:', error);
    }
}

// Book Management
async function loadBooks() {
    try {
        const response = await fetch('/api/books');
        const books = await response.json();
        renderBooks(books);
    } catch (error) {
        console.error('Error loading books:', error);
    }
}

async function searchBooks() {
    const title = document.getElementById('searchTitle').value;
    if (!title) {
        loadBooks();
        return;
    }
    try {
        const response = await fetch(`/api/books/title/${encodeURIComponent(title)}`);
        const books = await response.json();
        renderBooks(books);
    } catch (error) {
        console.error('Error searching books:', error);
    }
}

function renderBooks(books) {
    const bookTableBody = document.querySelector('#bookTable tbody');
    bookTableBody.innerHTML = '';
    books.forEach(book => {
        const row = document.createElement('tr');
        const statusClass = book.rented ? 'status-rented' : 'status-available';
        const statusText = book.rented ? '대출 중' : '대출 가능';

        let rowHTML = `
            <td>${book.id}</td>
            <td>${book.category}</td>
            <td>${book.title}</td>
            <td class="${statusClass}">${statusText}</td>
        `;

        if (typeof currentUserRole !== 'undefined' && currentUserRole === 'admin') {
            const returnDate = book.returnDate ? book.returnDate : '-';
            const userId = book.userId ? book.userId : '-';
            rowHTML += `
                <td>${returnDate}</td>
                <td>${userId}</td>
            `;
        }

        rowHTML += `
            <td>
                ${!book.rented ? 
                    `<button class="action-btn rent-btn" onclick="rentBook('${book.id}')">대출</button>` : 
                    `<button class="action-btn return-btn" onclick="returnBook('${book.id}')">반납</button>`
                }
            </td>
        `;

        row.innerHTML = rowHTML;
        bookTableBody.appendChild(row);
    });
}

async function rentBook(bookId) {
    const userId = prompt('대출할 회원 ID를 입력하세요:');
    if (!userId) return;

    try {
        const response = await fetch(`/api/books/${bookId}/checkout?userId=${encodeURIComponent(userId)}`, {
            method: 'POST'
        });
        if (response.ok) {
            alert('대출 성공!');
            loadBooks();
        } else {
            const errorText = await response.text();
            alert('대출 실패: ' + errorText);
        }
    } catch (error) {
        console.error('Error renting book:', error);
        alert('대출 중 오류가 발생했습니다.');
    }
}

async function returnBook(bookId) {
    const userId = prompt('반납할 회원 ID를 입력하세요:');
    if (!userId) return;

    try {
        const response = await fetch(`/api/books/${bookId}/checkin?userId=${encodeURIComponent(userId)}`, {
            method: 'POST'
        });
        if (response.ok) {
            alert('반납 성공!');
            loadBooks();
        } else {
            const errorText = await response.text();
            alert('반납 실패: ' + errorText);
        }
    } catch (error) {
        console.error('Error returning book:', error);
        alert('반납 중 오류가 발생했습니다.');
    }
}
