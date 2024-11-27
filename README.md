# GenderBiasAnalyzer
_A modest demonstration of how LLMs can assist in detecting biases in datasets._

## Objective and Motivation
This is a tool to check datasets for implicit gender bias. It demonstrates how LLM's can potentiallay assist in bringing AI Governance into practice, ensuring compliance with European laws for equal treatment of people regardless of religion, belief, political affiliation, race, and gender. An automated approach to detect bias within datasets can be helpful for developers and deployers of AI systems. Detecting bias is essential for compliance and fairness, aligning AI systems with organizational and legal requirements from the outset, in an early stage of development.

## Challenges and Importance of Bias Detection
Early bias detection during development is crucial to comply with legal obligations and prevent negative outcomes. Bias should be addressed early to avoid legal and societal repercussions. Implicit biases originate from dataset patterns, explicitly removing information on someones gender doesn't eliminate discrimination risks. Historical biases in datasets perpetuate discrimination in AI systems. Men and women may be classified unfairly based on proxy variables reflecting past societal biases. Such hidden biases are easily overlooked, but LLM's can be helpful in exploring the risk of such bias within datasets.

## How to launch...
The tool is designed to work from a webbrowser, but can be hosted locally on your computer or own network. It is not required to share your data.

### Step 1:
Launch with windows-cpu.cmd. This scripts will download a LLM, launch your browser and open http://localhost:8383/index.html
Linux and MacOS scripts will be shared later.

### Step 2:
Select a dataset sample, collected from [https://kaggle.com](https://www.kaggle.com/datasets). Or enter your own dataset sample. This will be uploaded to your own server running locally on your machine, or wherever you launched the script. The data is not stored anywhere.

### Step 3:

Wait for the algoritm to process your dataset description. Each variable from the description is recognized and analysed for potential risk of (implicit) bias on gender, based on knowlegde from literature on gender bias. Articles that were found usefull in the context of this particular dataset will be cited.

