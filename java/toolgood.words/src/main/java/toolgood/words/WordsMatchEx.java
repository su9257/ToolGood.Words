package toolgood.words;

import java.util.ArrayList;
import java.util.List;

import toolgood.words.internals.BaseMatchEx;

public class WordsMatchEx extends BaseMatchEx {

    /// <summary>
    /// 在文本中查找第一个关键字
    /// </summary>
    /// <param name="text">文本</param>
    /// <returns></returns>
    public WordsSearchResult FindFirst(String text) {
        int p = 0;
        for (int i = 0; i < text.length(); i++) {
            Character t1 = text.charAt(i);
            int t = _dict[t1];

            if (t == 0) {
                p = 0;
                continue;
            }
            int next;
            if (p == 0 || t < _min[p] || t > _max[p]) {
                next = _firstIndex[t];
            } else {
                int index = _nextIndex[p].IndexOf(t);
                if (index == -1) {
                    if (_wildcard[p] > 0) {
                        WordsSearchResult r = FindFirst(text, i + 1, _wildcard[p]);
                        if (r != null) {
                            return r;
                        }
                    }
                    next = _firstIndex[t];
                } else {
                    next = _nextIndex[p].GetValue(index);
                }
            }
            if (next != 0) {
                int start = _end[next];
                if (start < _end[next + 1]) {
                    int length = _keywordLength[_resultIndex[start]];
                    int s = i - length + 1;
                    if (s >= 0) {
                        String key = text.substring(s, i + 1);
                        int index = _resultIndex[start];
                        String matchKeyword = _matchKeywords[index];
                        return new WordsSearchResult(key, i + 1 - key.length(), i, index,matchKeyword);
                    }
                }
            }
            p = next;
        }
        return null;
    }

    private WordsSearchResult FindFirst(String text, int index, int p) {
        for (int i = index; i < text.length(); i++) {
            Character t1 = text.charAt(i);
            int t = _dict[t1];
            if (t == 0) {
                return null;
            }
            int next;
            if (p == 0 || t < _min[p] || t > _max[p]) {
                next = _firstIndex[t];
            } else {
                int index2 = _nextIndex[p].IndexOf(t);
                if (index2 == -1) {
                    if (_wildcard[p] > 0) {
                        WordsSearchResult r = FindFirst(text, i + 1, _wildcard[p]);
                        if (r != null) {
                            return r;
                        }
                    }
                    return null;
                } else {
                    next = _nextIndex[p].GetValue(index2);
                }
            }
            int start = _end[next];
            if (start < _end[next + 1]) {
                int length = _keywordLength[_resultIndex[start]];
                int s = i - length + 1;
                if (s >= 0) {
                    String key = text.substring(s, i + 1);
                    int index2 = _resultIndex[start];
                    String matchKeyword = _matchKeywords[index2];
                    return new WordsSearchResult(key, i + 1 - key.length(), i, index2,matchKeyword);
                }
            }
            p = next;
        }
        return null;
    }

    /// <summary>
    /// 在文本中查找所有的关键字
    /// </summary>
    /// <param name="text">文本</param>
    /// <returns></returns>
    public List<WordsSearchResult> FindAll(String text) {
        List<WordsSearchResult> result = new ArrayList<WordsSearchResult>();
        int p = 0;

        for (int i = 0; i < text.length(); i++) {
            Character t1 = text.charAt(i);

            int t = _dict[t1];
            if (t == 0) {
                p = 0;
                continue;
            }
            int next;
            if (p == 0 || t < _min[p] || t > _max[p]) {
                next = _firstIndex[t];
            } else {
                int index2 = _nextIndex[p].IndexOf(t);
                if (index2 == -1) {
                    if (_wildcard[p] > 0) {
                        FindAll(text, i + 1, _wildcard[p], result);
                    }
                    next = _firstIndex[t];
                } else {
                    next = _nextIndex[p].GetValue(index2);
                }
            }

            if (next != 0) {
                for (int j = _end[next]; j < _end[next + 1]; j++) {
                    int length = _keywordLength[_resultIndex[j]];
                    int s = i - length + 1;
                    if (s >= 0) {
                        int kIndex = _keywordIndex[j];
                        String matchKeyword = _matchKeywords[kIndex];
                        String key = text.substring(s, i + 1);
                        WordsSearchResult r = new WordsSearchResult(key, s, i, kIndex, matchKeyword);
                        result.add(r);
                    }
                }
            }
            p = next;
        }
        return result;
    }

    private void FindAll(String text, int index, int p, List<WordsSearchResult> result) {
        for (int i = index; i < text.length(); i++) {
            Character t1 = text.charAt(i);
            int t = _dict[t1];
            if (t == 0) {
                return;
            }
            int next;
            if (p == 0 || t < _min[p] || t > _max[p]) {
                next = _firstIndex[t];
            } else {
                int index2 = _nextIndex[p].IndexOf(t);
                if (index2 == -1) {
                    if (_wildcard[p] > 0) {
                        FindAll(text, i + 1, _wildcard[p], result);
                    }
                    return;
                } else {
                    next = _nextIndex[p].GetValue(index2);
                }
            }

            for (int j = _end[next]; j < _end[next + 1]; j++) {
                int length = _keywordLength[_resultIndex[j]];
                int s = i - length + 1;
                if (s >= 0) {
                    int kIndex = _keywordIndex[j];
                    String matchKeyword = _matchKeywords[kIndex];
                    String key = text.substring(s, i + 1);
                    WordsSearchResult r = new WordsSearchResult(key, s, i, kIndex, matchKeyword);
                    result.add(r);
                }
            }
            p = next;
        }
    }

    /// <summary>
    /// 判断文本是否包含关键字
    /// </summary>
    /// <param name="text">文本</param>
    /// <returns></returns>
    public boolean ContainsAny(String text)
    {
        int p = 0;
        for (int i = 0; i < text.length(); i++) {
            Character t1 = text.charAt(i);
            int t = _dict[t1];

            if (t == 0) {
                p = 0;
                continue;
            }
            int next;
            if (p == 0 || t < _min[p] || t > _max[p]) {
                next = _firstIndex[t];
            } else {
                int index = _nextIndex[p].IndexOf(t);
                if (index == -1) {
                    if (_wildcard[p] > 0) {
                        boolean r = ContainsAny(text, i + 1, _wildcard[p]);
                        if (r) {
                            return true;
                        }
                    }
                    next = _firstIndex[t];
                } else {
                    next = _nextIndex[p].GetValue(index);
                }
            }

            if (next != 0) {
                if (_end[next] < _end[next + 1]) {
                    return true;
                }
            }
            p = next;
        }
        return false;
    }

    private boolean ContainsAny(String text, int index, int p)
    {
        for (int i = index; i < text.length(); i++) {
            Character t1 = text.charAt(i);

            int t = _dict[t1];
            if (t == 0) {
                return false;
            }
            int next;
            if (p == 0 || t < _min[p] || t > _max[p]) {
                next = _firstIndex[t];
            } else {
                int index2 = _nextIndex[p].IndexOf(t);
                if (index2 == -1) {
                    if (_wildcard[p] > 0) {
                        boolean r = ContainsAny(text, i + 1, _wildcard[p]);
                        if (r) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    next = _nextIndex[p].GetValue(index2);
                }
            }
 
            int start = _end[next];
            if (start < _end[next + 1]) {
                int length = _keywordLength[_resultIndex[start]];
                int s = i - length + 1;
                if (s >= 0) {
                    return true;
                }
            }
            p = next;
        }
        return false;
    }

    /// <summary>
    /// 在文本中替换所有的关键字
    /// </summary>
    /// <param name="text">文本</param>
    /// <param name="replaceChar">替换符</param>
    /// <returns></returns>
    public String Replace(String text, char replaceChar  )
    {
        StringBuilder result = new StringBuilder(text);

        int p = 0;

        for (int i = 0; i < text.length(); i++) {
            Character t1 = text.charAt(i);
            int t = _dict[t1];

            if (t == 0) {
                p = 0;
                continue;
            }
            int next;
            if (p == 0 || t < _min[p] || t > _max[p]) {
                next = _firstIndex[t];
            } else {
                int index2 = _nextIndex[p].IndexOf(t);
                if (index2 == -1) {
                    if (_wildcard[p] > 0) {
                        Replace(text, i + 1, _wildcard[p],replaceChar, result);
                    }
                    next = _firstIndex[t];
                } else {
                    next = _nextIndex[p].GetValue(index2);
                }
            }

            if (next != 0) {
                int start = _end[next];
                if (start < _end[next + 1]) {
                    int maxLength = _keywordLength[_resultIndex[start]];
                    int start2 = i + 1 - maxLength;
                    if (start2 >= 0) {
                        for (int j = start2; j <= i; j++) {
                            result.setCharAt(j, replaceChar);
                        }
                    }
                }
            }
            p = next;
        }
        return result.toString();
    }

    private void Replace(String text, int index, int p, char replaceChar, StringBuilder result)
    {
        for (int i = index; i < text.length(); i++) {
            Character t1 = text.charAt(i);
 
            int t = _dict[t1];
            if (t == 0) {
                return;
            }
            int next;
            if (p == 0 || t < _min[p] || t > _max[p]) {
                next = _firstIndex[t];
            } else {
                int index2 = _nextIndex[p].IndexOf(t);
                if (index2 == -1) {
                    if (_wildcard[p] > 0) {
                        Replace(text, i + 1, _wildcard[p],replaceChar, result);
                    }
                    return;
                } else {
                    next = _nextIndex[p].GetValue(index2);
                }
            }
 
            int start = _end[next];
            if (start < _end[next + 1]) {
                int maxLength = _keywordLength[_resultIndex[start]];
                int start2 = i + 1 - maxLength;
                if (start2 >= 0) {
                    for (int j = start2; j <= i; j++) {
                        result.setCharAt(j, replaceChar);
                    }
                }
            }
            p = next;
        }
    }

}