# News Article Recommendation System


>[!important]
The News Article Recommendation System collects news articles from multiple sources, using Natural Language Processing (NLP) and Machine Learning (ML) techniques to analyze user preferences based on the articles they have previously read or rated. The system recommends new articles to the user, adapting its suggestions over time as the user's interests evolve.

## Key Features

1. **User Management**: 
   - Users can create an account, log in, and manage their preferences and reading history.
   - The system tracks interactions such as articles read, liked, or skipped to improve recommendations.

2. **Article Processing and Categorization**:
   - Articles are fetched from files, a database, or APIs and categorized using NLP techniques.
   - Categories include, but are not limited to, **Technology**, **Health**, **Sports**, and **AI**.
   - Basic algorithms for article classification include keyword extraction and ML-based categorization.

3. **Recommendation Engine**:
   - Recommends articles based on user reading history using collaborative filtering or content-based filtering.
   - The engine adapts to user preferences, improving recommendations over time.

4. **AI/ML Model Integration**:
   - The system integrates pre-trained ML models (e.g., for sentiment analysis or article categorization).
   - A learning algorithm is used to adjust recommendations according to user preferences.

5. **Exception and File Handling**:
   - Robust exception handling for invalid inputs, file I/O errors, and API failures.
   - The system uses file handling to store article data, user preferences, logs, and backup information.

6. **Concurrency**:
   - Designed to handle multiple user requests simultaneously, ensuring smooth operation.
   - Utilizes Java concurrency utilities to process large datasets, categorize articles, and generate recommendations in parallel.

## Technologies Used
- **Java**: Main programming language for backend services.
- **NLP & ML**: Techniques for categorizing articles and analyzing user preferences.
- **Concurrency**: Java concurrency utilities to handle multiple requests.
- **File I/O and Database**: For storing and managing user data and articles.

## Pre-Requisites
1. Ensure that you have ollama installed and setup, regard the following [link](https://ollama.com/download) to install ollama's local application
2. Proceed to install the following model through CMD after setting up the application by running the following command: `ollama run llama3.1`, [Model Documentation](https://ollama.com/library/llama3.1)
3. Thereafer you are required to create the following database through the following SQL queries:

```SQL
CREATE DATABASE personalized_news_system;

CREATE TABLE users (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    Read_Articles TEXT,
    Preferred_Genres TEXT
);

CREATE TABLE logged_in_users (
    id INT(11) AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE
);

```
4. Having done so, you should be ready to initiate the application

## Installation

To get started, clone this repository and follow the setup instructions below.

```bash
https://github.com/ethanchristoff/NLP-Based-News-Recommendation-System.git
```

Ensure you have Java 8 or higher installed. If you wish to use database integration, ensure you have the necessary drivers set up for your chosen database.

## Usage

1. **Create an account**: Users can register and log in.
2. **Read Articles**: Articles are categorized and stored for future recommendations.
3. **Get Recommendations**: The system generates article suggestions based on user history.
4. **Adjust Preferences**: The recommendation engine adapts over time based on user interactions.

## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/ethanchristoff/NLP-Based-News-Recommendation-System/blob/master/LICENSE) file for details.

---

For more information, refer to the complete documentation or explore the source code.
