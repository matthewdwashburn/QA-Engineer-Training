# pip install pandas, matplotlib

import pandas as pd
import matplotlib.pyplot as plt

def main() :
    df = pd.read_csv("sprint_remaining_points.csv")
    x = range(len(df))
    plt.figure(figsize=(8,4))
    print(len(df))
    plt.plot(x,df["ideal_remaining"],label="Ideal Remaining")
    plt.plot(x,df["actual_remaining"],label="actual Remaining")
    plt.xticks(x,df["day_label"])
    plt.ylabel("story points remaining")
    plt.xlabel("Sprint day")
    plt.title("Sprint burndown (sample week 5 data)")
    plt.legend()

    out = "burndown_sample.png"
    plt.savefig(out)
    print(f"Wrote {out}")

if __name__ == "__main__":
    main()

