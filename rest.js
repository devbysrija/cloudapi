
const express = require('express');
const app = express();

app.use(express.json());

let books = [
    { id: 1, title: 'Harry Potter' },
    { id: 2, title: 'Twilight' },
    { id: 3, title: 'Lorien Legacies' }
];

app.get('/api/books', (req, res) => {
    res.send(books);
});

app.get('/api/books/:id', (req, res) => {
    const book = books.find(b => b.id === parseInt(req.params.id));

    if (!book)
        return res.status(404).send('Book not found');

    res.send(book);
});

app.post('/api/books', (req, res) => {
    const book = {
        id: books.length ? Math.max(...books.map(b => b.id)) + 1 : 1,
        title: req.body.title
    };

    books.push(book);
    res.status(201).send(book);
});

app.put('/api/books/:id', (req, res) => {
    const book = books.find(b => b.id === parseInt(req.params.id));

    if (!book)
        return res.status(404).send('Book not found');

    book.title = req.body.title;
    res.send(book);
});

app.delete('/api/books/:id', (req, res) => {
    const book = books.find(b => b.id === parseInt(req.params.id));

    if (!book)
        return res.status(404).send('Book not found');

    books = books.filter(b => b.id !== book.id);
    res.send('Book deleted successfully');
});

const port = process.env.PORT || 8080;

app.listen(port, () => {
    console.log(`Server running on port ${port}`);
});
