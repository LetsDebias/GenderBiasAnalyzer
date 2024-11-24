# Dataset Analyser Tool

## Objective and Motivation
This is a tool that demonstrates that datasets can be checked for implicit bias, focusing initially on gender bias and later expanding to other biases. This tool aids in integrating AI Governance into practice, ensuring compliance with EU/NL laws for equal treatment regardless of religion, belief, political affiliation, race, gender, etc.

## Target Audience and AI Governance
A tool like this could be helpful for developers and deployers of AI systems. It helps meet AI Act mandates against discriminatory effects. Detecting bias is essential for compliance and fairness, aligning AI systems with organizational and legal requirements from the outset, in an early stage of development.

## Challenges and Importance of Bias Detection
AI and ICT developers often do not have insight into the inner workings of machine learning algorithms and AI models, especially machine learning ones. EU regulations emphasize the need for AI literacy. Early bias detection during development is crucial to comply with legal obligations and prevent negative outcomes. Bias should be addressed early to avoid legal and societal repercussions.

## Implicit and Historical Bias in Datasets
Implicit biases originate from dataset patterns, and removing gender data doesn't eliminate discrimination risks. Historical biases in datasets perpetuate discrimination in AI systems. Men and women may be classified unfairly based on proxy variables reflecting past societal biases.

## Technical Approach...
The tool relies on dataset descriptions similar to those found on Kaggle, making it unnecessary to analyze the dataset itself for identifying proxy variables.

### Step 1:
Go to http://localhost:8383/index.html

![Screenshot1](images/screenshot1.png)

### Step 2:
Enter a dataset description, similar in freeformat style as you would find them on [https://kaggle.com](https://www.kaggle.com/datasets). You can use the Example button to get an example description. When done, press Submit.

![Screenshot2](images/screenshot2.png)

### Step 3:

Wait for the algoritm to process your dataset description. Each variable from the description is recognized and analysed for potential risk of (implicit) bias on gender, based on knowlegde from literature on gender bias. Articles that were found usefull in the context of this particular dataset will be cited.

![Screenshot3](images/screenshot3.png)
