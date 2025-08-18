# Data Generation and Analysis Project

This project demonstrates how to generate dummy data using [Faker](https://faker.readthedocs.io/) and perform data analysis with Python.  
It uses **Python 3.11.7** and **pip 25.1.1**.

---

## 📌 Prerequisites

- Python **3.11.7** installed on your system  
- pip **25.1.1** (latest package installer for Python)  
- [virtualenv](https://docs.python.org/3/library/venv.html) (optional but recommended)

---

## ⚙️ Setup Instructions

### 1. Create and activate a virtual environment
```bash
# Create virtual environment
python3 -m venv venv

# Activate (Linux/Mac)
source venv/bin/activate

# Activate (Windows - PowerShell)
.\venv\Scripts\activate


🚀 Usage
1. Generate Dummy Data

We use the Faker library to generate synthetic/dummy data.

python generate_dummy_data.py


This will create a dataset with randomly generated names, addresses, dates, and other information depending on your script’s configuration.

2. Run Data Analysis

Once the dummy dataset is generated, you can run the analysis:

python data_analysis.py


This script will:

Load the generated dataset

Perform exploratory data analysis (EDA)

Provide insights such as data distributions, statistics, and trends

📂 Project Structure
project-root/
│── requirements.txt        # Python dependencies
│── generate_dummy_data.py  # Script to generate random data
│── data_analysis.py        # Script to analyze the generated data
│── README.md               # Documentation
│── venv/                   # Virtual environment (ignored in git)

🛠 Example Dependencies (requirements.txt)
faker
pandas
numpy
matplotlib
seaborn

📊 Example Outputs

Generated Data: A CSV or JSON file with random records

Analysis: Graphs, descriptive statistics, and insights printed or saved

📝 Notes

Ensure you always activate the virtual environment before running scripts.

You can extend generate_dummy_data.py to include more complex data models (e.g., user profiles, transactions).

The analysis script can be customized to suit your dataset needs.