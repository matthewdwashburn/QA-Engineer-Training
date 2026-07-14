import pandas as pd

def filter_dataframe(df, col, func): 
    if col not in df.columns:
        raise ValueError(f"Column '{col}' is not present in the DataFrame.")
    col_series = pd.Series(df[col])
    valid_rows = col_series.apply(func)
    new_df = df[~valid_rows]
    return new_df